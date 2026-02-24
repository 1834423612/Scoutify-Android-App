package com.team695.scoutifyapp.data.api.model

import com.google.gson.annotations.SerializedName
import com.team695.scoutifyapp.data.extensions.convertIsoToUnix
import com.team695.scoutifyapp.db.MatchEntity
import java.util.Date

data class Match(
    @SerializedName("gm_number")
    val matchNumber: Int,
    @SerializedName("gm_game_type")
    val gameType: String,
    @SerializedName("gm_timestamp")
    val time: String,
    @SerializedName(value="R1")
    private val r1: Int,
    @SerializedName(value="R2")
    private val r2: Int,
    @SerializedName(value="R3")
    private val r3: Int,
    @SerializedName(value="B1")
    private val b1: Int,
    @SerializedName(value="B2")
    private val b2: Int,
    @SerializedName(value="B3")
    private val b3: Int,
) {
    val redAlliance: List<Int>
        get() = listOf(r1, r2, r3)
    val blueAlliance: List<Int>
        get() = listOf(b1, b2, b3)

    val unixTime: Long
        get() = time.convertIsoToUnix()
}

fun MatchEntity.createMatchFromDb(): Match {
    return Match(
        matchNumber = this.matchNumber.toInt(),
        gameType = this.gameType,
        time = this.time.toString(),
        r1 = this.r1.toInt(),
        r2 = this.r2.toInt(),
        r3 = this.r3.toInt(),
        b1 = this.b1.toInt(),
        b2 = this.b2.toInt(),
        b3 = this.b3.toInt()
    )
}