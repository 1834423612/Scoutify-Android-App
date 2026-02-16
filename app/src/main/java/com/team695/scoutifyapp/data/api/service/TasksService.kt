package com.team695.scoutifyapp.data.api.service

import com.team695.scoutifyapp.db.AppDatabase
import com.team695.scoutifyapp.data.api.model.Task
import com.team695.scoutifyapp.data.api.model.TaskType
class TaskService(val db: AppDatabase) {
    fun getTasks(): List<Task> {
        println( db.taskQueries
            .selectAllTasks().executeAsList())
        return db.taskQueries
            .selectAllTasks()
            .executeAsList()
            .map { entity ->
                println("TASK: ${entity}")
                Task(
                    id = entity.id.toInt(),
                    type = TaskType.SCOUTING,
                    matchNum = 0,
                    teamNum = entity.title,
                    time = "01m",
                    progress = 1.0f,
                    isDone = entity.isCompleted == 1L
                )
            }
    }

    fun insertTask(task: Task) {
        db.taskQueries.insertTask(
            title = task.teamNum,
            isCompleted = if (task.isDone) 1 else 0
        )
    }

    fun deleteTask(id: Long) {
        db.taskQueries.deleteTaskById(id)
    }
}
