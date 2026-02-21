package com.team695.scoutifyapp.data.types

//in milliseconds
val TRANSITION_END_TIME: Int = 10 * 1000
val SHIFT1_END_TIME: Int = 35 * 1000
val SHIFT2_END_TIME: Int = 60 * 1000
val SHIFT3_END_TIME: Int = 75 * 1000
val SHIFT4_END_TIME: Int = 100 * 1000
val ENDGAME_END_TIME: Int = 130 * 1000

/*
    maximum difference in milliseconds between expected and actual times for allowing users
    to switch to the next teleop section (otherwise warn them)
 */
val TELEOP_TIME_THRESHOLD = 4500