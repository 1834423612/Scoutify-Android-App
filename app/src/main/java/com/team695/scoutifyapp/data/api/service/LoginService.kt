package com.team695.scoutifyapp.data.api.service

import com.team695.scoutifyapp.data.api.model.LoginBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface LoginService {
    @POST("login/oauth/authorize?" +
        "client_id={client_id}" +
        "&response_type=code" +
        "&scope=profile email openid" +
        "&state={appName}" +
        "&code_challenge_method=S256" +
        "&code_challenge{challenge}" +
        "&redirect_uri={redirectUri}"
    )

    suspend fun login(
        @QueryMap params: Map<String, String>,
        @Body body: LoginBody
    ): ResponseBody
}