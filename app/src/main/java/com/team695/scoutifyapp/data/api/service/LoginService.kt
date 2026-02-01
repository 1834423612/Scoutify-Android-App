package com.team695.scoutifyapp.data.api.service

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("login")
    suspend fun login(@Body ): ResponseBody
}