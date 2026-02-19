package com.team695.scoutifyapp.data.api.service

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Header

data class UserInfoResponse(
    val name: String? = null,
    @SerializedName("preferred_username") val preferredUsername: String? = null,
    val picture: String? = null,
    val email: String? = null,
    @SerializedName("android_id") val androidId: String? = null
)

interface UserService {
    @GET("api/getUserInfo")
    suspend fun getUserInfo(
        @Header("Authorization") authHeader: String
    ): UserInfoResponse
}