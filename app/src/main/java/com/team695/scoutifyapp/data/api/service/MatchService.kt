package com.team695.scoutifyapp.data.api.service

import com.team695.scoutifyapp.data.api.model.Match
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Query

interface MatchService {
    @GET("scoutify/game-matchups")
    suspend fun listMatches(
        @Query("bearerAuth") acToken: String,
        @Query("apiKeyAuth") acKey: String,
        @Query("apiSecretAuth") secret: String,
        @Query("smYear") year: Int = 2025,
        @Query("eventCode") eventCode: String = "ohcl",
        @Query("gameType") gameType: Char = 'Q',
    ): ApiResponse<List<Match>>
}