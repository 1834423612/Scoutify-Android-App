package com.team695.scoutifyapp.data.api.service

import retrofit2.http.GET
import retrofit2.http.Header

interface CasdoorUserInfoService {
    @GET("api/userinfo")
    suspend fun getUserInfo(
        @Header("Authorization") authHeader: String
    ): Map<String, Any?>
}
