package com.team695.scoutifyapp.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.team695.scoutifyapp.data.api.model.GameConstantsStore
import com.team695.scoutifyapp.data.api.model.ServerFormatTask
import com.team695.scoutifyapp.data.api.model.Task
import com.team695.scoutifyapp.data.api.model.TaskType
import com.team695.scoutifyapp.data.api.model.convertToServerFormat
import com.team695.scoutifyapp.data.api.model.createTaskFromDb
import com.team695.scoutifyapp.data.api.service.LoginService
import com.team695.scoutifyapp.data.api.service.TaskService
import com.team695.scoutifyapp.db.AppDatabase
import com.team695.scoutifyapp.db.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.text.first

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
        .flowOn(Dispatchers.IO)



    suspend fun pushTasks(): List<ServerFormatTask> {
        fun convert(Task: TaskEntity): ServerFormatTask{
            val gameConstants = GameConstantsStore.constants   // non-null

            val matchNumber = Task.matchNum
            val teamNumber = Task.teamNum.toLong()
            val gameType = db.matchQueries
                .selectMatchByNumberAndTeam(matchNumber, teamNumber)
                .executeAsOne().gameType
            val user: String = db.userQueries.selectUser().executeAsOne().name ?: ""
            return Task.convertToServerFormat(gameConstants,teamNumber.toInt(),user,695,gameType[0])
        }
        return db.taskQueries.selectAllTasks().executeAsList().map {
            convert(it)
        }

    }

    suspend private fun updateDbFromTaskList(tasks: List<Task>) {
        withContext(Dispatchers.IO) {
            db.transaction {
                tasks.forEach {
                    db.taskQueries.insertTask(
                        id = it.id.toLong(),
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
    }

    suspend fun fetchTasks(): Result<List<Task>> {
        return withContext(Dispatchers.IO) {
            val oldTasks = db.taskQueries.selectAllTasks()
                .executeAsList()
                .map { entity ->
                    entity.createTaskFromDb()
                }

            try {
                val apiTasks: List<Task> = service.getTasks()

                if (apiTasks.isNotEmpty()) {
                    updateDbFromTaskList(apiTasks)
                }

                return@withContext Result.success(apiTasks)
            } catch(e: Exception) {

                println("Error when trying to fetch tasks: $e")
                updateDbFromTaskList(oldTasks)
                return@withContext Result.failure(e)
            }
        }
    }

    suspend fun getTaskById(taskId: Int): Result<Task> {

        return withContext(Dispatchers.IO) {
            val taskList: List<TaskEntity> = db.taskQueries.selectTaskById(taskId.toLong())
                .executeAsList()

            if (taskList.isNotEmpty()) {
                return@withContext Result.success(taskList[0].createTaskFromDb())
            } else {
                return@withContext Result.failure(Exception("Could not find task for id = $taskId"))
            }
        }
    }
}