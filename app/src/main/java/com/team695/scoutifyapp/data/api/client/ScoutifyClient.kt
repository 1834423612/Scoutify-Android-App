package com.team695.scoutifyapp.data.api.client

import android.content.Context
import com.team695.scoutifyapp.data.api.TokenManager
import com.team695.scoutifyapp.data.api.service.CommentService
import com.team695.scoutifyapp.data.api.service.GameDetailsService
import com.team695.scoutifyapp.data.api.service.MatchService
import com.team695.scoutifyapp.data.api.service.SurveyService
import com.team695.scoutifyapp.data.api.service.TaskService
import com.team695.scoutifyapp.data.api.service.TeamNameService
import com.team695.scoutifyapp.data.api.service.UserService
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ScoutifyClient {
    private const val BASE_URL = "https://api.team695.com/"
    private lateinit var retrofit: Retrofit
    lateinit var tokenManager: TokenManager

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

    val taskService: TaskService by lazy {
        retrofit.create(TaskService::class.java)
    }

    val gameDetailsService: GameDetailsService by lazy {
        retrofit.create(GameDetailsService::class.java)
    }

    val commentService: CommentService by lazy {
        retrofit.create(CommentService::class.java)
    }

    val teamNameService: TeamNameService by lazy {
        retrofit.create(TeamNameService::class.java)
    }

    val surveyService: SurveyService by lazy {
        retrofit.create(SurveyService::class.java)
    }
}
