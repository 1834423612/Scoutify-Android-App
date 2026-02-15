package com.team695.scoutifyapp.data.api.service

import com.team695.scoutifyapp.data.api.model.Match
import okhttp3.ResponseBody
import retrofit2.http.GET

interface MatchService {
    @GET("match-listing")
    suspend fun listMatches(): List<Match>
}