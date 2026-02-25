package com.team695.scoutifyapp.data.api.service

import com.google.gson.annotations.SerializedName
import com.team695.scoutifyapp.BuildConfig
import com.team695.scoutifyapp.data.api.model.GameConstants
import com.team695.scoutifyapp.data.api.model.GameConstantsStore
import com.team695.scoutifyapp.data.api.model.ServerFormatTask
import com.team695.scoutifyapp.db.AppDatabase
import com.team695.scoutifyapp.data.api.model.Task
import com.team695.scoutifyapp.data.api.model.createTaskFromDb
import retrofit2.http.GET
import retrofit2.http.Query

interface TaskService {
    @GET("scoutify/event-tasks")
    suspend fun getTasks(
        @Query("bearerAuth") acToken: String,
        @Query("apiKeyAuth") acKey: String = BuildConfig.API_AC_KEY,
        @Query("apiSecretAuth") secret: String = BuildConfig.API_AC_KEY,
        @Query("smYear") year: Int =
            GameConstantsStore.constants.frc_season_master_sm_year,
        @Query("eventCode") eventCode: String
            = GameConstantsStore.constants.competition_master_cm_event_code,
        @Query("gameType") gameType: Char
            = GameConstantsStore.constants.game_matchup_gm_game_type,
    ): ApiResponseWithRows<List<ServerFormatTask>>
}