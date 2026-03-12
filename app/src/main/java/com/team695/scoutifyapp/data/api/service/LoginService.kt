package com.team695.scoutifyapp.data.api.service

import com.google.gson.annotations.SerializedName
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

data class TokenResponse(
    @SerializedName("access_token") final val accessToken: String
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
}