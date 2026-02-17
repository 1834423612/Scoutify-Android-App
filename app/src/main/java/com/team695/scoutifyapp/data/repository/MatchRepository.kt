package com.team695.scoutifyapp.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.team695.scoutifyapp.data.api.model.Match
import com.team695.scoutifyapp.data.api.model.Task
import com.team695.scoutifyapp.data.api.model.createMatchFromDb
import com.team695.scoutifyapp.data.api.model.createTaskFromDb
import com.team695.scoutifyapp.data.api.service.TaskService
import com.team695.scoutifyapp.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MatchRepository(
    private val service: TaskService,
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
                db.taskQueries.insertTask(
                    type = it.type.toString(),
                    matchNum = it.matchNum.toLong(),
                    teamNum = it.teamNum,
                    time = it.time,
                    progress = it.progress.toDouble(),
                    isDone = if (it.isDone) 1L else 0L
                )
            }
        }
    }

    suspend fun fetchMatches(): Result<Boolean> {
        val oldTasks = db.taskQueries.selectAllTasks()
            .executeAsList()
            .map { entity ->
                entity.createTaskFromDb()
            }

        withContext(Dispatchers.IO) {
            try {
                val apiTasks: List<Task> = service.getTasks()

                if (apiTasks.isNotEmpty()) {
                    updateDbFromTaskList(apiTasks)
                }

                return@withContext Result.success(true)
            } catch(e: Exception) {
                println("Error when trying to fetch tasks: $e")
                updateDbFromTaskList(oldTasks)
                return@withContext Result.failure(e)
            }
        }

        return Result.failure(Exception())
    }
}