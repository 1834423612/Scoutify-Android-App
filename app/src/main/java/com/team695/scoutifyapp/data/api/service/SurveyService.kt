package com.team695.scoutifyapp.data.api.service

import com.team695.scoutifyapp.BuildConfig
import com.team695.scoutifyapp.data.types.SurveyResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SurveyService {
    @POST("survey/submit")
    suspend fun submitSurveyResponse(
        @Body surveyResponse: SurveyResponse,
        @Query("bearerAuth") acToken: String,
        @Query("apiKeyAuth") acKey: String = BuildConfig.API_AC_KEY,
        @Query("apiSecretAuth") secret: String = BuildConfig.API_AC_SECRET
    ): ApiResponse<Map<String, Any?>>

    @GET("survey/query")
    suspend fun querySurveyResponses(
        @Query("eventId") eventId: String,
        @Query("bearerAuth") acToken: String,
        @Query("apiKeyAuth") acKey: String = BuildConfig.API_AC_KEY,
        @Query("apiSecretAuth") secret: String = BuildConfig.API_AC_SECRET
    ): ApiResponse<List<SurveyResponse>>
}
