package com.team695.scoutifyapp.data

data class TeamInfo(
    val teamNumber: Int,
    val teamName: String
)

val sampleTeams = listOf(
    TeamInfo(695, "Bison Robotics"),
    TeamInfo(1114, "Simbotics"),
    TeamInfo(254, "The Cheesy Poofs")
)
