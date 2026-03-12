package com.team695.scoutifyapp.data.extensions
import java.time.Instant

fun String.convertIsoToUnix(): Long {
    try {
        return Instant.parse(this).toEpochMilli()
    } catch (e: Exception) {
        // println("Error converting time to unix: $e")
        return 0L
    }
}