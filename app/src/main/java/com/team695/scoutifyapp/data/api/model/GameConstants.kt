package com.team695.scoutifyapp.data.api.model
import com.google.gson.annotations.SerializedName

data class GameConstants (
    @SerializedName(value="year")
    val frc_season_master_sm_year: Int,
    @SerializedName(value="event_code")
    val competition_master_cm_event_code: String,
    @SerializedName(value="game_type")
    val game_matchup_gm_game_type: Char
)

object GameConstantsStore {
    var _constants: GameConstants? = null

    val constants: GameConstants
        get() = _constants ?: error("GameConstants not initialized")

    fun set(value: GameConstants) {
        _constants = value
    }

    init {
        set(GameConstants(
            frc_season_master_sm_year = 2026,
            competition_master_cm_event_code = "test",
            game_matchup_gm_game_type = 'Q'
        ))
    }
}
