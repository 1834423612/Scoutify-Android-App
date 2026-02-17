package com.team695.scoutifyapp.data.api.model

import com.team695.scoutifyapp.db.TaskEntity
import java.util.Date

data class Task(
    val id: Int,
    val type: TaskType,
    val matchNum: Int,
    val teamNum: String,
    val time: Long,
    val progress: Float, // 0.0 to 1.0
    val isDone: Boolean = false,
)

fun TaskEntity.createTaskFromDb(): Task {
    return Task(
        id = this.id.toInt(),
        type = TaskType.valueOf(this.type),
        matchNum = this.matchNum.toInt(),
        teamNum = this.teamNum,
        time = this.time,
        progress = this.progress.toFloat(),
        isDone = this.isDone == 1L
    )
}