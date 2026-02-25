package com.team695.scoutifyapp.data.api.model
import com.google.gson.annotations.SerializedName

data class GameConstants (
    val frc_season_master_sm_year: Int,
    val competition_master_cm_event_code: String,
    val game_matchup_gm_game_type: Char
)

object GameConstantsStore {
    private var _constants: GameConstants? = null

    val constants: GameConstants
        get() = _constants ?: error("GameConst  ants not initialized")

    fun set(value: GameConstants) {
        _constants = value
    }

    init {
        /*
        set(GameConstants(
            frc_season_master_sm_year = 2026,
            competition_master_cm_event_code = "test",
            game_matchup_gm_game_type = 'Q'
        ))
         */
    }
}
