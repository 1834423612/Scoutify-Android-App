package com.team695.scoutifyapp.data

enum class TaskType {
    SCOUTING,
    PIT,
}

enum class TaskProgress(val percent: Float) {
    INCOMPLETE(0f),
    PARTIAL(.5f),
    COMPLETE(1f)
}

data class Task(
    val id: Int,
    val type: TaskType,
    val matchNum: Int,
    val teamNum: String,
    val time: String,
    val progress: Float, // 0.0 to 1.0
    val isDone: Boolean = false,
    val taskCompPercentString: String
)