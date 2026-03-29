package com.team695.scoutifyapp.data.api.service

import com.team695.scoutifyapp.BuildConfig
import com.team695.scoutifyapp.data.api.model.GameConstants
import com.team695.scoutifyapp.data.api.model.GameConstantsStore
import com.team695.scoutifyapp.data.api.model.GameDetails
import com.team695.scoutifyapp.data.api.model.Match
import retrofit2.http.Body
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
        @Body gameDetails: List<GameDetails>
    ): ApiResponse<Any>

    suspend fun listDetails(
        @Query("bearerAuth") acToken: String,
        @Query("apiKeyAuth") acKey: String
        = BuildConfig.API_AC_KEY,
        @Query("apiSecretAuth") secret: String
        = BuildConfig.API_AC_SECRET,
        @Query("smYear") year: Int
        = GameConstantsStore.constants.frc_season_master_sm_year,
        @Query("eventCode") eventCode: String
        = GameConstantsStore.constants.competition_master_cm_event_code,
        @Query("gameType") gameType: Char
        = GameConstantsStore.constants.game_matchup_gm_game_type,
    ): ApiResponse<List<GameDetails?>>
}