package com.team695.scoutifyapp.data.api.model

import com.team695.scoutifyapp.db.TaskEntity
import java.util.Date

class Task(
    val id: Int,
    val type: TaskType,
    val matchNum: Int,
    val teamNum: String,
    val time: Long,
    val progress: Float, // 0.0 to 1.0
    val isDone: Boolean = false,
): Comparable<Task> {
    override fun compareTo(other: Task): Int {
        if (time != other.time) {
            return if ((time - other.time) > 0) 1 else -1
        }

        println("${type.ordinal}, ${other.type.ordinal}, type: ${type.toString()} ")

        return type.ordinal - other.type.ordinal
    }
}

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