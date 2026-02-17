package com.team695.scoutifyapp.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.team695.scoutifyapp.data.api.model.Task
import com.team695.scoutifyapp.data.api.model.TaskType
import com.team695.scoutifyapp.data.api.model.createTaskFromDb
import com.team695.scoutifyapp.data.api.service.LoginService
import com.team695.scoutifyapp.data.api.service.TaskService
import com.team695.scoutifyapp.db.AppDatabase
import com.team695.scoutifyapp.db.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class TaskRepository(
    private val service: TaskService,
    private val db: AppDatabase,
) {
    val tasks: Flow<List<Task>> = db.taskQueries.selectAllTasks()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { entities ->
            entities.map { entity ->
                entity.createTaskFromDb()
            }
        }


    suspend fun pushTasks() {
        //service.pushTasks(tasks.first())
    }

    private fun updateDbFromTaskList(tasks: List<Task>) {
        db.transaction {
            db.taskQueries.clearAllTasks()

            tasks.forEach {
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

    suspend fun fetchTasks(): Result<Boolean> {
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