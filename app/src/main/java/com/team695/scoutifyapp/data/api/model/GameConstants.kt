package com.team695.scoutifyapp.data.api.model
import com.google.gson.annotations.SerializedName

data class GameConstants (
    @SerializedName(value="year")
    public val frc_season_master_sm_year: Int,
    @SerializedName(value="event_code")
    public val competition_master_cm_event_code: String
)
object gameConstantsInitial{
    public val frc_season_master_sm_year = 2026
    public val competition_master_cm_event_code = "mnwi"
}