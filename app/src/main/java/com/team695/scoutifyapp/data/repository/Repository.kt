package com.team695.scoutifyapp.data.repository

interface Repository {
    suspend fun push(): Result<Any> = Result.success(Unit)

    suspend fun fetch(): Result<Any> = Result.success(Unit)
}