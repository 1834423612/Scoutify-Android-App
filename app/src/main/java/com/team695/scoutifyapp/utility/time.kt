package com.team695.scoutifyapp.utility

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun displayTime(T: Long): String {
    return Instant.ofEpochMilli(T).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("HH:mm"))
}