package com.team695.scoutifyapp.ui.extensions

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long.minDiffFromNow(): Long {
    val diff = this - System.currentTimeMillis()

    return ((diff / 1000) / 60).coerceIn(-99L, 99L)
}

val formatter = DateTimeFormatter.ofPattern("HH:mm")

fun Long.convertUnixToMilitaryTime(): String {
    val instant = Instant.ofEpochMilli(this)

    val localTime = instant.atZone(ZoneId.systemDefault())

    return localTime.format(formatter)
}

fun Long.convertUnixToIso(): String {
    return try {
        Instant.ofEpochMilli(this).toString()
    } catch (e: Exception) {
        // println("Error converting unix to ISO string: $e")
        ""
    }
}