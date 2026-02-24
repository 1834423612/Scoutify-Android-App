package com.team695.scoutifyapp.data.api.service

import com.team695.scoutifyapp.BuildConfig
import com.team695.scoutifyapp.db.AppDatabase
import com.team695.scoutifyapp.data.api.model.Task
import com.team695.scoutifyapp.data.api.model.createTaskFromDb
import retrofit2.http.GET
import retrofit2.http.Query

interface TaskService {
    @GET("scoutify/event-assignments")
    suspend fun getTasks(
        @Query("bearerAuth") acToken: String,
        @Query("apiKeyAuth") acKey: String = BuildConfig.API_AC_KEY,
        @Query("apiSecretAuth") secret: String = BuildConfig.API_AC_KEY,
        @Query("smYear") year: Int = 2025,
        @Query("eventCode") eventCode: String = "ohcl",
        @Query("gameType") gameType: Char = 'Q',
    ): ApiResponseWithRows<List<Task>>
}

