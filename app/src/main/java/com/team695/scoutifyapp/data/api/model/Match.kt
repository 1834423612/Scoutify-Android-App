package com.team695.scoutifyapp.data.api.model

import com.google.gson.annotations.SerializedName
import com.team695.scoutifyapp.db.MatchEntity
import java.util.Date

data class Match(
    @SerializedName("gm_number")
    val matchNumber: Int,
    @SerializedName("gm_game_type")
    val gameType: String,
    @SerializedName("gm_timestamp")
    val time: Long,
    @SerializedName(value="r1_team_number")
    private val r1: Int,
    @SerializedName(value="r2_team_number")
    private val r2: Int,
    @SerializedName(value="r3_team_number")
    private val r3: Int,
    @SerializedName(value="b1_team_number")
    private val b1: Int,
    @SerializedName(value="b2_team_number")
    private val b2: Int,
    @SerializedName(value="b3_team_number")
    private val b3: Int,
) {
    val redAlliance: List<Int> = listOf(r1, r2, r3)
    val blueAlliance: List<Int> = listOf(b1, b2, b3)
}

fun MatchEntity.createMatchFromDb(): Match {
    return Match(
        matchNumber = this.matchNumber.toInt(),
        gameType = this.gameType,
        time = this.time,
        r1 = this.r1.toInt(),
        r2 = this.r2.toInt(),
        r3 = this.r3.toInt(),
        b1 = this.b1.toInt(),
        b2 = this.b2.toInt(),
        b3 = this.b3.toInt()
    )
}