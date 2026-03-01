package com.team695.scoutifyapp.data.api.service

import com.team695.scoutifyapp.BuildConfig
import com.team695.scoutifyapp.data.api.model.GameConstants
import com.team695.scoutifyapp.data.api.model.GameDetailsActions
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface GameDetailsService {
    @GET("scoutify/game-constants")
    suspend fun getGameConstants(
        @Query("bearerAuth") acToken: String,
        @Query("apiKeyAuth") acKey: String
        = BuildConfig.API_AC_KEY,
        @Query("apiSecretAuth") secret: String
        = BuildConfig.API_AC_SECRET,
    ): ApiResponse<GameConstants>

    @POST("scoutify/admin/game-details")
    suspend fun updateGameDetails(
        @Header("Authorization") acToken: String,
        @Body gameDetails: List<GameDetailsActions>
    ): ApiResponse<Any>
}