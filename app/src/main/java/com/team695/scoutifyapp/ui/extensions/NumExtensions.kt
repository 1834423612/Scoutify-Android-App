package com.team695.scoutifyapp.ui.extensions

fun Long.minDiffFromNow(): Long {
    val diff = this - System.currentTimeMillis()

    println("DIFFERENCE: $diff, ${System.currentTimeMillis()}, ${this}")

    return ((diff / 1000) / 60).coerceAtMost(99L)
}