package com.team695.scoutifyapp.data.api.service

import com.team695.scoutifyapp.db.AppDatabase
import com.team695.scoutifyapp.data.api.model.Task
import com.team695.scoutifyapp.data.api.model.TaskType
import com.team695.scoutifyapp.data.api.model.createTaskFromDb

class TaskService(val db: AppDatabase) {
    fun getTasks(): List<Task> {
        return db.taskQueries
            .selectAllTasks()
            .executeAsList()
            .map { entity ->
                println("TASK: ${entity}")
                entity.createTaskFromDb()
            }
    }

    fun insertTask(task: Task) {
        db.taskQueries.insertTask(
            id = task.id.toLong(),
            type = task.type.toString(),
            matchNum = task.matchNum.toLong(),
            teamNum = task.teamNum,
            time = task.time,
            progress = task.progress.toDouble(),
            isDone = if (task.isDone) 1L else 0L
        )
    }

    fun deleteTask(id: Long) {
        db.taskQueries.deleteTaskById(id)
    }
}
