package com.team695.scoutifyapp.data.api.service

import com.team695.scoutifyapp.BuildConfig
import com.team695.scoutifyapp.data.api.model.Match
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Query

interface MatchService {
    @GET("scoutify/game-matchups")
    suspend fun listMatches(
        @Query("bearerAuth") acToken: String,
        @Query("apiKeyAuth") acKey: String = BuildConfig.API_AC_KEY,
        @Query("apiSecretAuth") secret: String = BuildConfig.API_AC_SECRET,
        @Query("smYear") year: Int = 2025,
        @Query("eventCode") eventCode: String = "ohcl",
        @Query("gameType") gameType: Char = 'Q',
    ): ApiResponse<List<Match>>
}