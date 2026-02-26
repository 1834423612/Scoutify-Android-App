package com.team695.scoutifyapp.data.api.service

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Header

data class UserInfoResponse(
    @SerializedName("um_id") val name: String? = null,
    @SerializedName("um_email") val email: String? = null,
    @SerializedName("um_name") val displayName: String? = null,
    @SerializedName("um_android_device_id") val androidID: String? = null
)

interface UserService {
    @GET("scoutify/user/me")
    suspend fun getUserInfo(
        @Header("Authorization") authHeader: String
    ): ApiResponse<UserInfoResponse>
}