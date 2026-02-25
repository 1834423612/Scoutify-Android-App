package com.team695.scoutifyapp.data.api.service

import com.team695.scoutifyapp.BuildConfig
import com.team695.scoutifyapp.data.api.model.GameConstants
import retrofit2.http.GET
import retrofit2.http.Query

interface GameDetailsService {
    @GET("scoutify/game-constants")
    suspend fun setGameConstants(
        @Query("bearerAuth") acToken: String,
        @Query("apiKeyAuth") acKey: String
        = BuildConfig.API_AC_KEY,
        @Query("apiSecretAuth") secret: String
        = BuildConfig.API_AC_SECRET,
    ): ApiResponse<GameConstants>
}