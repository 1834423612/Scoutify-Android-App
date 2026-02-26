package com.team695.scoutifyapp.data.api.service

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?
)

data class ApiResponseWithRows<T>(
    val success: Boolean,
    val data: RowsPayload<T>
)

data class RowsPayload<T>(
    val rows: T?
)