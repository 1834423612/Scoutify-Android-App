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
            db.taskQueries.clearAllTasks()

            matches.forEach {
                db.matchQueries.insertMatch(
                    time = it.time.toLong(),
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

    suspend fun fetchMatches(): Result<Boolean> {
        val oldMatches = db.matchQueries.selectAllMatches()
            .executeAsList()
            .map { entity ->
                entity.createMatchFromDb()
            }

        withContext(Dispatchers.IO) {
            try {
                val apiTasks: List<Match> = service.listMatches()

                if (apiTasks.isNotEmpty()) {
                    updateDbFromMatchList(apiTasks)
                }

                return@withContext Result.success(true)
            } catch(e: Exception) {
                println("Error when trying to fetch tasks: $e")
                updateDbFromMatchList(oldMatches)
                return@withContext Result.failure(e)
            }
        }

        return Result.failure(Exception())
    }
}