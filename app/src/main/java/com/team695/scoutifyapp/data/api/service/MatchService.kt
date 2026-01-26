package com.team695.scoutifyapp.data.api.service

import com.team695.scoutifyapp.data.api.model.Match
import retrofit2.http.GET

public interface MatchService {
    @GET("matches")
    suspend fun listMatches(): List<Match>
}