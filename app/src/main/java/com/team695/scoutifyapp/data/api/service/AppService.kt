package com.team695.scoutifyapp.data.api.service

import com.team695.scoutifyapp.BuildConfig
import com.team695.scoutifyapp.data.api.model.GithubResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AppService {
    @GET("repos/1834423612/Scoutify-Android-app/releases/latest")
    suspend fun getAssets(): GithubResponse
}