package com.team695.scoutifyapp.data.api.client

import android.content.Context
import com.team695.scoutifyapp.data.api.TokenManager
import com.team695.scoutifyapp.data.api.service.MatchService
import com.team695.scoutifyapp.data.api.service.UserService
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ScoutifyClient {
    lateinit var tokenManager: TokenManager
    private const val BASE_URL = "http://scoutify.team695.com/"
    private lateinit var retrofit: Retrofit

    fun initialize(context: Context) {
        tokenManager = TokenManager(context.applicationContext)

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()

                val token = runBlocking {
                    tokenManager.getToken()
                }

                val request = if (token != null) {
                    original.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                } else {
                    original
                }
                chain.proceed(request)
            }
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val matchService: MatchService by lazy {
        retrofit.create(MatchService::class.java)
    }

    val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }
}