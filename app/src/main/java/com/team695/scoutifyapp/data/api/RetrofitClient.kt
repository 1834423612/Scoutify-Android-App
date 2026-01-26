package com.team695.scoutifyapp.data.api

import com.team695.scoutifyapp.data.api.service.MatchService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ScoutifyClient {
    private const val BASE_URL = "https://scoutify.team695.com/api/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val matchService: MatchService by lazy {
        retrofit.create(MatchService::class.java)
    }
}