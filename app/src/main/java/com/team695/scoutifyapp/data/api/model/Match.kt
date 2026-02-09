package com.team695.scoutifyapp.data.api.model

// TODO: Add a comprehensive team data class
data class Match(
    final val blueAlliance: List<Team> = emptyList(),
    final val redAlliance: List<Team> = emptyList()
)