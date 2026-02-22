package com.team695.scoutifyapp.data.api.model

import com.google.gson.annotations.SerializedName

data class CommentBody (
    @SerializedName("game_matchup_gm_number")
    val match_number: Int,
    val team_number: Int,
    @SerializedName("game_matchup_gm_alliance")
    val alliance: String, // B/R
    @SerializedName("game_matchup_gm_alliance_position")
    val alliance_position: Int, // 1, 2, 3
    @SerializedName("gc_comment")
    val comment: String,
    @SerializedName("gc_ts")
    val timestamp: Long,
    val submitted: Boolean = false
)
