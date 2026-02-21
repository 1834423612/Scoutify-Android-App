package com.team695.scoutifyapp.data.api.client

import com.team695.scoutifyapp.data.api.service.LoginService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CasdoorClient {
    private const val BASE_URL = "https://sso.team695.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val loginService: LoginService by lazy {
        retrofit.create(LoginService::class.java)
    }
}