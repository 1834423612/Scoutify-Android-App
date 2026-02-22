package com.team695.scoutifyapp.data.api.service

import com.team695.scoutifyapp.data.api.model.GameConstants
import retrofit2.http.GET

interface GameDetailsService {
    @GET("scoutify/game-matchups")
    suspend fun setGameConstants(): GameConstants
}