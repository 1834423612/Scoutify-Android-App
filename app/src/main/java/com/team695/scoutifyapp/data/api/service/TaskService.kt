package com.team695.scoutifyapp.data.api.service

import com.google.gson.annotations.SerializedName
import com.team695.scoutifyapp.BuildConfig
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
        @Query("smYear") year: Int = 2026,
        @Query("eventCode") eventCode: String = "test",
        @Query("gameType") gameType: Char = 'Q',
    ): ApiResponseWithRows<List<ServerFormatTask>>
}