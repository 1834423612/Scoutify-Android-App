package com.team695.scoutifyapp.data.api.client

import com.team695.scoutifyapp.data.api.service.AppService
import com.team695.scoutifyapp.data.api.service.LoginService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GithubClient {
    private const val BASE_URL = "https://api.github.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val appService: AppService by lazy {
        GithubClient.retrofit.create(AppService::class.java)
    }
}