package com.team695.scoutifyapp.data.api.service

import com.team695.scoutifyapp.db.AppDatabase
import com.team695.scoutifyapp.data.api.model.Task
import com.team695.scoutifyapp.data.api.model.createTaskFromDb
import retrofit2.http.GET

interface TaskService {
    @GET("scoutify/event-assignments")
    suspend fun getTasks(): List<Task>
}

