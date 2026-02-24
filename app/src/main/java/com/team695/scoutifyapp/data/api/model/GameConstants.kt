package com.team695.scoutifyapp.data.api.model
import com.google.gson.annotations.SerializedName

data class GameConstants (
    @SerializedName(value="year")
    public val frc_season_master_sm_year: Int,
    @SerializedName(value="event_code")
    public val competition_master_cm_event_code: String
)

object GameConstantsStore {
    public var _constants: GameConstants? = null

    val constants: GameConstants
        get() = _constants ?: error("GameConstants not initialized")

    fun set(value: GameConstants) {
        _constants = value
    }
}
