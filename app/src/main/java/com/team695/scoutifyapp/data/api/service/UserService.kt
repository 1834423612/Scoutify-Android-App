package com.team695.scoutifyapp.data.api.service

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header

interface UserService {
    @GET("api/userinfo")
    suspend fun getUserInfo(
        @Header("Authorization") authHeader: String
    ): UserInfoResponse
}