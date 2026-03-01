package com.team695.scoutifyapp.data.types

enum class TaskStatus {
    IN_PROGRESS,
    INCOMPLETE,
    DONE
}

data class Task(
    val id: Int,
    val teamNumber: String,
    val eventName: String,
    val timeRemaining: String,
    val progressPercent: Int,
    val status: TaskStatus
)
