package com.team695.scoutifyapp.data.repository

import android.content.Context
import android.util.Log
import android.util.Base64
import android.webkit.CookieManager
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.team695.scoutifyapp.BuildConfig
import com.team695.scoutifyapp.data.api.NetworkMonitorStatus
import com.team695.scoutifyapp.data.api.client.ScoutifyClient
import com.team695.scoutifyapp.data.api.model.User
import com.team695.scoutifyapp.data.api.service.ApiResponse
import com.team695.scoutifyapp.data.api.service.CasdoorUserInfoService
import com.team695.scoutifyapp.data.api.service.LoginService
import com.team695.scoutifyapp.data.api.service.TokenResponse
import com.team695.scoutifyapp.data.api.service.UserInfoResponse
import com.team695.scoutifyapp.data.api.service.UserService
import com.team695.scoutifyapp.db.AppDatabase
import com.team695.scoutifyapp.ui.extensions.androidID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

data class UserAccessProfile(
    val groups: List<String> = emptyList(),
    val roles: List<String> = emptyList(),
    val permissions: List<String> = emptyList(),
    val isAdmin: Boolean = false,
    val canManageDatabase: Boolean = false,
)

class UserRepository(
    private val loginService: LoginService,
    private val casdoorUserInfoService: CasdoorUserInfoService,
    private val userService: UserService,
    private val db: AppDatabase,
    private val context: Context,
) {
    private val gson = Gson()

    var currentUser: Flow<User?> = db.userQueries.selectUser()
        .asFlow()
        .mapToOneOrNull(Dispatchers.IO)
        .map { entity ->
            if (entity == null) return@map null

            User(
                name = entity.name,
                displayName = entity.display_name,
                email = entity.email,
                androidID = entity.android_id
            )
        }
        .flowOn(Dispatchers.IO)

    suspend fun getUserInfo(): Boolean {
        return withContext(Dispatchers.IO) {
            val token = ScoutifyClient.tokenManager.getToken().orEmpty()
            if (token.isBlank()) {
                return@withContext false
            }

            try {
                val claims = getIdentityClaims(token)
                val appProfile = runCatching {
                    userService.getUserInfo(authHeader = "Bearer $token")
                }.getOrNull()

                val userRes: UserInfoResponse? = appProfile?.data
                val mergedName = firstNonBlank(
                    userRes?.name,
                    claims["preferred_username"].asTrimmedString(),
                    claims["username"].asTrimmedString(),
                    claims["name"].asTrimmedString(),
                    claims["sub"].asTrimmedString()
                )
                val mergedDisplayName = firstNonBlank(
                    userRes?.displayName,
                    claims["displayName"].asTrimmedString(),
                    claims["fullName"].asTrimmedString(),
                    claims["name"].asTrimmedString(),
                    claims["preferred_username"].asTrimmedString(),
                    mergedName
                )
                val mergedEmail = firstNonBlank(
                    userRes?.email,
                    claims["email"].asTrimmedString()
                )
                val mergedAndroidId = firstNonBlank(
                    userRes?.androidID,
                    claims["um_android_device_id"].asTrimmedString(),
                    claims["androidID"].asTrimmedString(),
                    claims["deviceId"].asTrimmedString()
                )
                val hasIdentityData = listOf(mergedName, mergedDisplayName, mergedEmail, mergedAndroidId)
                    .any { !it.isNullOrBlank() }
                val deviceMatches = mergedAndroidId.isNullOrBlank() ||
                    mergedAndroidId == context.androidID ||
                    BuildConfig.DEBUG

                if (!hasIdentityData) {
                    Log.d("User", "No identity fields returned from app profile or Casdoor userinfo.")
                    return@withContext false
                }

                if (deviceMatches) {
                    persistUser(
                        name = mergedName,
                        displayName = mergedDisplayName,
                        email = mergedEmail,
                        androidId = mergedAndroidId
                    )

                    return@withContext true
                } else {
                    LocalDatabaseWriteCoordinator.withWriteLock {
                        db.userQueries.insertUser(
                            name = "WRONG_USER",
                            display_name = "WRONG_USER",
                            email = "WRONG_USER",
                            android_id = "WRONG_USER"
                        )
                    }

                    return@withContext false
                }
            } catch (e: Exception) {
                Log.d("User", "Error when trying to get user info: $e")
                return@withContext false
            }
        }
    }

    suspend fun getAccessToken(code: String, verifier: String): TokenResponse {
        return loginService.getAccessToken(
            clientSecret = BuildConfig.CASDOOR_CLIENT_SECRET,
            clientId = BuildConfig.CASDOOR_CLIENT_ID,
            code = code,
            verifier = verifier
        )
    }

    suspend fun getAccessProfile(): UserAccessProfile {
        val token = ScoutifyClient.tokenManager.getToken().orEmpty()
        val claims = getIdentityClaims(token)
        val groups = normalizeClaimArray(claims["groups"])
        val roles = normalizeClaimArray(claims["roles"])
        val permissions = normalizeClaimArray(claims["permissions"])
        val normalizedClaims = groups + roles + permissions

        val isAdmin = claims["is_admin"].asBooleanFlag() ||
            claims["admin"].asBooleanFlag() ||
            normalizedClaims.any(::looksLikeAdminClaim)

        val canManageDatabase = isAdmin || permissions.any(::looksLikeDatabasePermission)

        return UserAccessProfile(
            groups = groups,
            roles = roles,
            permissions = permissions,
            isAdmin = isAdmin,
            canManageDatabase = canManageDatabase
        )
    }

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            NetworkMonitorStatus.currentNetworkJob?.let { activeJob ->
                runCatching {
                    activeJob.cancelAndJoin()
                }.onFailure { error ->
                    Log.d("User", "Failed to stop network sync cleanly during logout: $error")
                }
                NetworkMonitorStatus.currentNetworkJob = null
            }

            LocalDatabaseWriteCoordinator.withWriteLock {
                db.transaction {
                    db.userQueries.deleteUser()
                    db.matchQueries.clearAllMatches()
                    db.taskQueries.clearAllTasks()
                    db.commentsQueries.clearAllComments()
                    db.pitscoutQueries.clearAllPitscout()
                    db.pitScoutingTabQueries.clearAllTabs()
                }
            }

            ScoutifyClient.tokenManager.saveToken("")

            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
        }
    }

    private suspend fun getTokenClaims(): Map<String, Any?> {
        val token = ScoutifyClient.tokenManager.getToken().orEmpty()
        return getTokenClaims(token)
    }

    private suspend fun getIdentityClaims(token: String): Map<String, Any?> {
        val tokenClaims = getTokenClaims(token)
        if (token.isBlank()) {
            return tokenClaims
        }

        val userInfoClaims = runCatching {
            casdoorUserInfoService.getUserInfo(authHeader = "Bearer $token")
        }.onFailure { error ->
            Log.d("User", "Failed to fetch Casdoor userinfo: $error")
        }.getOrDefault(emptyMap())

        return tokenClaims + userInfoClaims
    }

    private fun getTokenClaims(token: String): Map<String, Any?> {
        val payload = token.split('.').getOrNull(1).orEmpty()
        if (payload.isBlank()) {
            return emptyMap()
        }

        return runCatching {
            val normalizedPayload = payload.padEnd(payload.length + (4 - payload.length % 4) % 4, '=')
            val decodedPayload = String(Base64.decode(normalizedPayload, Base64.URL_SAFE or Base64.NO_WRAP))
            val type = object : TypeToken<Map<String, Any?>>() {}.type
            gson.fromJson<Map<String, Any?>>(decodedPayload, type) ?: emptyMap()
        }.getOrDefault(emptyMap())
    }

    private suspend fun persistUser(
        name: String?,
        displayName: String?,
        email: String?,
        androidId: String?,
    ) {
        LocalDatabaseWriteCoordinator.withWriteLock {
            db.userQueries.insertUser(
                name = name,
                display_name = displayName,
                email = email,
                android_id = androidId
            )
        }
    }

    private fun normalizeClaimArray(raw: Any?): List<String> {
        return when (raw) {
            is List<*> -> raw.mapNotNull { item -> item?.toString()?.trim() }.filter { it.isNotBlank() }
            is Array<*> -> raw.mapNotNull { item -> item?.toString()?.trim() }.filter { it.isNotBlank() }
            is String -> raw.split(',').map { it.trim() }.filter { it.isNotBlank() }
            else -> emptyList()
        }
    }

    private fun Any?.asBooleanFlag(): Boolean {
        return when (this) {
            is Boolean -> this
            is Number -> this.toInt() != 0
            is String -> this.equals("true", ignoreCase = true) || this == "1"
            else -> false
        }
    }

    private fun Any?.asTrimmedString(): String? {
        return this?.toString()?.trim()?.takeIf { it.isNotBlank() }
    }

    private fun looksLikeAdminClaim(value: String): Boolean {
        val normalized = value.trim().lowercase()
        val adminKeywords = listOf("admin", "administrator", "superuser", "root")
        return adminKeywords.any { keyword ->
            normalized == keyword || normalized.contains(keyword)
        }
    }

    private fun looksLikeDatabasePermission(value: String): Boolean {
        val normalized = value.trim().lowercase()
        val referencesDatabase = normalized.contains("database") ||
            Regex("""(^|[:._-])db([:._-]|$)""").containsMatchIn(normalized)
        val grantsDatabaseAccess = listOf(
            "manage",
            "admin",
            "debug",
            "inspect",
            "view",
            "read",
            "write"
        ).any(normalized::contains)

        return referencesDatabase && grantsDatabaseAccess
    }

    private fun firstNonBlank(vararg values: String?): String? {
        return values.firstOrNull { !it.isNullOrBlank() }?.trim()
    }
}
