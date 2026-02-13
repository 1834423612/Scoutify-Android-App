package com.team695.scoutifyapp.data.api.service

import com.google.gson.annotations.SerializedName
import com.team695.scoutifyapp.data.api.model.LoginBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.QueryMap

data class TokenResponse(
    @SerializedName("access_token") final val accessToken: String
)

data class UserInfoResponse(
    val name: String?,
    @SerializedName("preferred_username") val preferredUsername: String?,
    val picture: String?,
    val email: String?
)

interface LoginService {
    @FormUrlEncoded
    @POST("api/login/oauth/access_token")
    suspend fun getAccessToken(
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("code_verifier") verifier: String
    ): TokenResponse

    @GET("api/userinfo")
    suspend fun getUserInfo(
        @Header("Authorization") authHeader: String
    ): UserInfoResponse
}