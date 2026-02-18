package com.team695.scoutifyapp.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.team695.scoutifyapp.data.api.model.GameDetails
import com.team695.scoutifyapp.data.api.model.createGameDetailsFromDb
import com.team695.scoutifyapp.data.api.service.GameDetailsService
import com.team695.scoutifyapp.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameDetailRepository(
    private val service: GameDetailsService,
    private val db: AppDatabase,
) {
    val gameDetails: Flow<List<GameDetails>> = db.gameDetailsQueries.selectAllGameDetails()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { entries ->
            entries.map { entity ->
                entity.createGameDetailsFromDb()
            }
        }

    private fun updateDbFromGameDetails(details: List<GameDetails>) {

    }

    /*suspend fun fetchGameDetails(): Result<List<Match>> {

    }*/
}