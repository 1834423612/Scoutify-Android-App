package com.team695.scoutifyapp.data.api.service

import com.team695.scoutifyapp.data.api.model.LoginBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("login")
    suspend fun login(@Body body: LoginBody): ResponseBody
}