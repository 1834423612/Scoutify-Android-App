package com.team695.scoutifyapp.data.api.service

import okhttp3.ResponseBody
import retrofit2.http.GET

interface MatchService: ServiceInterface {
    @GET("match-listing")
    suspend fun listMatches(): ResponseBody
}