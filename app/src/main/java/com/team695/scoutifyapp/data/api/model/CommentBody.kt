package com.team695.scoutifyapp.data.api.model

import com.google.gson.annotations.SerializedName
import com.team695.scoutifyapp.db.CommentsEntity

data class CommentBody (
    @SerializedName("game_matchup_gm_number")
    val match_number: Int,
    val team_number: Int,
    @SerializedName("game_matchup_gm_alliance")
    val alliance: String?, // B/R
    @SerializedName("game_matchup_gm_alliance_position")
    val alliance_position: Int?, // 1, 2, 3
    @SerializedName("gc_comment")
    val comment: String?,
    @SerializedName("gc_ts")
    val timestamp: Long?,
    val submitted: Int? = 0 // 0-false, 1-true
)

data class CommentServerBody(
    val frc_season_master_sm_year: Int,
    val competition_master_cm_event_code: String,
    val game_matchup_gm_game_type: Char,
    val game_matchup_gm_number: Int,
    val game_matchup_gm_alliance: Char,
    val game_matchup_gm_alliance_position: Int,
    val gc_comment: String,
)

fun CommentsEntity.convertToServerBody(): CommentServerBody {
    return CommentServerBody(
        frc_season_master_sm_year = GameConstantsStore.constants.frc_season_master_sm_year,
        competition_master_cm_event_code =
            GameConstantsStore.constants.competition_master_cm_event_code,
        game_matchup_gm_game_type = GameConstantsStore.constants.game_matchup_gm_game_type,
        game_matchup_gm_number = match_number,
        game_matchup_gm_alliance = alliance!![0],
        game_matchup_gm_alliance_position = alliance_position!!,
        gc_comment = comment ?: ""
    )
}