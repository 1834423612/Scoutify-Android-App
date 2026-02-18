package com.team695.scoutifyapp.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.team695.scoutifyapp.data.api.model.Match
import com.team695.scoutifyapp.data.api.model.Task
import com.team695.scoutifyapp.data.api.model.createMatchFromDb
import com.team695.scoutifyapp.data.api.model.createTaskFromDb
import com.team695.scoutifyapp.data.api.service.MatchService
import com.team695.scoutifyapp.data.api.service.TaskService
import com.team695.scoutifyapp.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MatchRepository(
    private val service: MatchService,
    private val db: AppDatabase,
) {
    val matches: Flow<List<Match>> = db.matchQueries.selectAllMatches()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { entries ->
            entries.map { entity ->
                entity.createMatchFromDb()
            }
        }

    private fun updateDbFromMatchList(matches: List<Match>) {
        db.transaction {
            matches.forEach {
                db.matchQueries.insertMatch(
                    time = it.time,
                    matchNumber = it.matchNumber.toLong(),
                    gameType = it.gameType,
                    r1 = it.redAlliance[0].toLong(),
                    r2 = it.redAlliance[1].toLong(),
                    r3 = it.redAlliance[2].toLong(),
                    b1 = it.blueAlliance[0].toLong(),
                    b2 = it.blueAlliance[1].toLong(),
                    b3 = it.blueAlliance[2].toLong(),
                )
            }
        }
    }

    suspend fun fetchMatches(): Result<List<Match>> {
        val oldMatches = db.matchQueries.selectAllMatches()
            .executeAsList()
            .map { entity ->
                entity.createMatchFromDb()
            }

        return withContext(Dispatchers.IO) {
            try {
                val apiMatches: List<Match> = service.listMatches()

                if (apiMatches.isNotEmpty()) {
                    updateDbFromMatchList(apiMatches)
                }

                return@withContext Result.success(apiMatches)
            } catch(e: Exception) {
                println("Error when trying to fetch matches: $e")
                updateDbFromMatchList(oldMatches)
                return@withContext Result.failure(e)
            }
        }
    }
}