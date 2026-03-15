package com.team695.scoutifyapp.data.api.service

import com.team695.scoutifyapp.data.api.model.TeamNameResponse
import retrofit2.http.GET

interface TeamNameService {
    @GET("team/teams")
    suspend fun fetchTeamNames(): List<TeamNameResponse>
}