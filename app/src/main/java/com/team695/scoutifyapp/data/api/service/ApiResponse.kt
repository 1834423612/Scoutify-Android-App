package com.team695.scoutifyapp.data.api.service

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?
)