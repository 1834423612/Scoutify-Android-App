package com.team695.scoutifyapp.data.repository

import android.content.Context
import android.webkit.CookieManager
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.team695.scoutifyapp.BuildConfig
import com.team695.scoutifyapp.data.api.client.ScoutifyClient
import com.team695.scoutifyapp.data.api.model.User
import com.team695.scoutifyapp.data.api.service.ApiResponse
import com.team695.scoutifyapp.data.api.service.LoginService
import com.team695.scoutifyapp.data.api.service.TokenResponse
import com.team695.scoutifyapp.data.api.service.UserInfoResponse
import com.team695.scoutifyapp.data.api.service.UserService
import com.team695.scoutifyapp.db.AppDatabase
import com.team695.scoutifyapp.ui.extensions.androidID
import com.team695.scoutifyapp.ui.viewModels.LoginStatus
import com.team695.scoutifyapp.utility.displayTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class UserRepository(
    private val loginService: LoginService,
    private val userService: UserService,
    private val db: AppDatabase,
    private val context: Context,
) {

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

    suspend fun getUserInfo(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val reqRes: ApiResponse<UserInfoResponse> = userService.getUserInfo(
                    authHeader = "Bearer ${ScoutifyClient.tokenManager.getToken() ?: ""}"
                )

                val userRes: UserInfoResponse? = reqRes.data

                if (userRes?.androidID == context.androidID) {
                    db.userQueries.insertUser(
                        name = userRes.name,
                        display_name = userRes.displayName,
                        email = userRes.email,
                        android_id = userRes.androidID
                    )

                    return@withContext true
                } else {
                    println("CONTEXT: ${context.androidID}")

                    db.userQueries.insertUser(
                        name = "WRONG_USER",
                        display_name = "WRONG_USER",
                        email = "WRONG_USER",
                        android_id = "WRONG_USER"
                    )

                    return@withContext false
                }
            } catch (e: Exception) {
                println("Error when trying to get user info: $e")
                return@withContext true
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

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            db.userQueries.deleteUser()
            ScoutifyClient.tokenManager.saveToken("")

            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
        }
    }
}