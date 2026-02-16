package com.team695.scoutifyapp.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.team695.scoutifyapp.BuildConfig
import com.team695.scoutifyapp.data.api.ScoutifyClient
import com.team695.scoutifyapp.data.api.model.User
import com.team695.scoutifyapp.data.api.service.LoginService
import com.team695.scoutifyapp.data.api.service.TokenResponse
import com.team695.scoutifyapp.data.api.service.UserInfoResponse
import com.team695.scoutifyapp.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class UserRepository(
    private val service: LoginService,
    private val db: AppDatabase,
) {

    var currentUser: Flow<User?> = db.userQueries.selectUser()
        .asFlow()
        .mapToOneOrNull(Dispatchers.IO)
        .map { entity ->
            if (entity == null) return@map null

            User(
                name = entity.name,
                preferredUsername = entity.preferred_username,
                picture = entity.picture,
                email = entity.email
            )
        }

    suspend fun getUserInfo() {
        withContext(Dispatchers.IO) {
            val userRes: UserInfoResponse = service.getUserInfo(
                authHeader = "Bearer ${ScoutifyClient.tokenManager.getToken() ?: ""}"
            )

            db.userQueries.insertUser(
                name = userRes.name,
                preferred_username = userRes.preferredUsername,
                picture = userRes.picture,
                email = userRes.email
            )
        }
    }

    suspend fun getAccessToken(code: String, verifier: String): TokenResponse {
        return service.getAccessToken(
            clientSecret = BuildConfig.CASDOOR_CLIENT_SECRET,
            clientId = BuildConfig.CASDOOR_CLIENT_ID,
            code = code,
            verifier = verifier
        )
    }
}