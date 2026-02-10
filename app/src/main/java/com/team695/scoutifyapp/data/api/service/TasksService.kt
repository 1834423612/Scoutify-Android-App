package com.team695.scoutifyapp.data.api.service

import com.team695.scoutifyapp.data.Task
import com.team695.scoutifyapp.data.TaskType

class TaskService {
    fun getTasks(): List<Task> {
        return listOf(
            Task(2, TaskType.SCOUTING, 3, "118", "01m", 0.9f, false),
            Task(3, TaskType.SCOUTING, 4, "254", "03m", 1.0f, true),
            Task(4, TaskType.SCOUTING, 5, "148", "02m", 0.1f, false),
            Task(5, TaskType.SCOUTING, 6, "971", "01m", 1.0f, true)
        )
    }
}