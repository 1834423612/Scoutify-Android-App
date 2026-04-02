package com.team695.scoutifyapp.data.api.service

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


data class PitAssignmentDto(
    val id: String? = null,
    @SerializedName("task_type") val taskType: String? = null,
    @SerializedName("assigned_team_numbers") val assignedTeamNumbers: List<Int>? = null
)

data class TeamPitStatusDto(
    @SerializedName("team_number") val teamNumber: Int? = null,
    @SerializedName("is_pit") val isPit: Boolean? = null
)

data class PitStatusUpdateRequest(
    @SerializedName("is_pit") val isPit: Boolean
)

data class UploadImageResponse(
    val id: String = "",
    val url: String = ""
)

data class TeamSuggestionDto(
    @SerializedName("team_number") val teamNumber: String? = null,
    @SerializedName("team_name") val teamName: String? = null,
    @SerializedName("teamNumber") val teamNumberAlt: String? = null,
    @SerializedName("teamName") val teamNameAlt: String? = null,
    val number: String? = null,
    val name: String? = null,
    val nickname: String? = null
)

data class CurrentEventResponse(
    val eventId: String = "",
    val eventName: String = "",
    val eventDate: String = ""
)

interface SurveyService {
    @POST("survey/submit")
    suspend fun submitSurveyResponse(
        @Body payload: Map<String, @JvmSuppressWildcards Any?>
    ): ApiResponse<Map<String, Any?>>

    @GET("survey/query")
    suspend fun querySurveyResponses(
        @Query("eventId") eventId: String
    ): ApiResponse<List<Map<String, @JvmSuppressWildcards Any?>>>

    @GET("assignments/user/{eventId}")
    suspend fun getUserAssignments(
        @Path("eventId") eventId: String
    ): ApiResponse<List<PitAssignmentDto>>

    @GET("team-matches/event/{eventId}")
    suspend fun getEventTeams(
        @Path("eventId") eventId: String
    ): ApiResponse<List<TeamPitStatusDto>>

    @PUT("team-matches/pit-status-by-number/{eventId}/{teamNumber}")
    suspend fun updatePitStatus(
        @Path("eventId") eventId: String,
        @Path("teamNumber") teamNumber: String,
        @Body request: PitStatusUpdateRequest
    ): ApiResponse<Map<String, Any?>>

    @Multipart
    @POST("api/upload/upload")
    suspend fun uploadPitImage(
        @Part file: MultipartBody.Part,
        @Part("type") type: RequestBody
    ): UploadImageResponse

    @DELETE("api/upload/images/{imageId}")
    suspend fun deletePitImage(
        @Path("imageId") imageId: String
    ): ApiResponse<Map<String, Any?>>

    @GET("api/team/teams")
    suspend fun searchTeams(
        @Query("query") query: String,
        @Query("limit") limit: Int = 20
    ): List<TeamSuggestionDto>

    @GET("api/event/event-id")
    suspend fun getCurrentEvent(): CurrentEventResponse
}
