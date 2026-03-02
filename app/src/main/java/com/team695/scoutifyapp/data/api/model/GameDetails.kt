package com.team695.scoutifyapp.data.api.model
import android.util.Log
import com.team695.scoutifyapp.db.GameDetailsEntity
import kotlin.Boolean
import kotlin.Double
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties


data class GameDetails(
    val id: Int? = null,
    val task_id: Int? = null,

    // Starting & Preload
    val startingLocation: Double? = null,
    val robotOnField: Boolean? = null,
    val robotPreloaded: Boolean? = null,
    val pregameFlag: Boolean? = null,
    // Auton
    val autonPath: String? = null,
    val autonAttemptsClimb: Boolean? = null,
    val autonClimbSuccess: Boolean? = null,
    val autonClimbPosition: String? = null,
    val autonFuelCount: Int? = null,
    val autonFlag: Boolean? = null,

    // Transition Shift
    val transitionCyclingTime: Int? = null,
    val transitionStockpilingTime: Int? = null,
    val transitionDefendingTime: Int? = null,
    val transitionBrokenTime: Int? = null,
    val transitionFirstActive: Boolean? = null,

    // 1st Shift
    val shift1CyclingTime: Int? = null,
    val shift1StockpilingTime: Int? = null,
    val shift1DefendingTime: Int? = null,
    val shift1BrokenTime: Int? = null,

    // 2nd Shift
    val shift2CyclingTime: Int? = null,
    val shift2StockpilingTime: Int? = null,
    val shift2DefendingTime: Int? = null,
    val shift2BrokenTime: Int? = null,

    // 3rd Shift
    val shift3CyclingTime: Int? = null,
    val shift3StockpilingTime: Int? = null,
    val shift3DefendingTime: Int? = null,
    val shift3BrokenTime: Int? = null,

    // 4th Shift
    val shift4CyclingTime: Int? = null,
    val shift4StockpilingTime: Int? = null,
    val shift4DefendingTime: Int? = null,
    val shift4BrokenTime: Int? = null,

    // Endgame
    val endgameCyclingTime: Int? = null,
    val endgameStockpilingTime: Int? = null,
    val endgameDefendingTime: Int? = null,
    val endgameBrokenTime: Int? = null,

    val endgameAttemptsClimb: Boolean? = null,
    val endgameClimbSuccess: Boolean? = null,
    val endgameClimbPosition: String? = null,
    val endgameClimbCode: String? = null,


    // Teleop
    val teleopFuelCount: Int? = null,
    val teleopFlag: Boolean? = null,
    val teleopCompleted: Boolean? = null,

    val postgameShootWhileMoving: Boolean? = null,
    val postgameStockpileNeutral: Boolean? = null,
    val postgameStockpileAlliance: Boolean? = null,
    val postgameStockpileCrossCourt: Boolean? = null,
    val postgameFeedOutpost: Boolean? = null,
    val postgameReceiveOutpost: Boolean? = null,
    val postgameUnderTrench: Boolean? = null,
    val postgameOverBump: Boolean? = null,
    val postgameShootAnywhere: Boolean? = null,
    val postgameFlag: Boolean? = null,
    // Review
    val reviewMatchFlag: Boolean? = null
) {
    val endgameClimbPositionFilled: Boolean get() {
        if(endgameClimbSuccess == true) {
            return !endgameClimbCode.isNullOrEmpty()
        }
        return true
    }

    val pregameProgress: Float get() {
        val pregameVars = listOf<Any?>(
            //startingLocation, //TO DO: add this
            robotOnField,
            robotPreloaded
        )
        return pregameVars.count( {it != null} ).toFloat() / pregameVars.size

    }

    // returns progress for endgame
    val endgameProgress: Float get() {
        val endgameVars = listOf<Any?>(
            endgameAttemptsClimb,
            endgameClimbSuccess,
        )
        val filledElements: Int = endgameVars.count( {it != null} ) + if(endgameClimbPositionFilled) 1 else 0
        val totalElements: Int = endgameVars.size + 1
        return filledElements.toFloat() / totalElements
    }

    val postgameProgress: Float get() {
        val postgameVars = listOf<Any?>(
            postgameShootWhileMoving,
            postgameStockpileNeutral,
            postgameStockpileAlliance,
            postgameStockpileCrossCourt,
            postgameFeedOutpost,
            postgameReceiveOutpost,
            postgameUnderTrench,
            postgameOverBump,
            postgameShootAnywhere,
        )
        return postgameVars.count( {it != null} ).toFloat() / postgameVars.size
    }
}


fun GameDetailsEntity.createGameDetailsFromDb(): GameDetails {
    return GameDetails(
        id = this.id,
        task_id = this.task_id,

        // Starting & Preload
        startingLocation = this.starting_location,
        robotOnField = this.robot_on_field,
        robotPreloaded = this.robot_preloaded,
        pregameFlag = this.pregame_flag,          // ADDED

        // Auton
        autonPath = this.auton_path,
        autonAttemptsClimb = this.auton_attempts_climb,
        autonClimbSuccess = this.auton_climb_success,
        autonClimbPosition = this.auton_climb_position,
        autonFuelCount = this.auton_fuel_count,
        autonFlag = this.auton_flag,              // ADDED

        // Transition Shift
        transitionCyclingTime = this.transition_cycling_time,
        transitionStockpilingTime = this.transition_stockpiling_time,
        transitionDefendingTime = this.transition_defending_time,
        transitionBrokenTime = this.transition_broken_time,
        transitionFirstActive = this.transition_first_active,

        // 1st Shift
        shift1CyclingTime = this.shift1_cycling_time,
        shift1StockpilingTime = this.shift1_stockpiling_time,
        shift1DefendingTime = this.shift1_defending_time,
        shift1BrokenTime = this.shift1_broken_time,

        // 2nd Shift
        shift2CyclingTime = this.shift2_cycling_time,
        shift2StockpilingTime = this.shift2_stockpiling_time,
        shift2DefendingTime = this.shift2_defending_time,
        shift2BrokenTime = this.shift2_broken_time,

        // 3rd Shift
        shift3CyclingTime = this.shift3_cycling_time,
        shift3StockpilingTime = this.shift3_stockpiling_time,
        shift3DefendingTime = this.shift3_defending_time,
        shift3BrokenTime = this.shift3_broken_time,

        // 4th Shift
        shift4CyclingTime = this.shift4_cycling_time,
        shift4StockpilingTime = this.shift4_stockpiling_time,
        shift4DefendingTime = this.shift4_defending_time,
        shift4BrokenTime = this.shift4_broken_time,

        // Endgame
        endgameCyclingTime = this.endgame_cycling_time,
        endgameStockpilingTime = this.endgame_stockpiling_time,
        endgameDefendingTime = this.endgame_defending_time,
        endgameBrokenTime = this.endgame_broken_time,
        endgameClimbCode = this.endgame_climb_code,

        endgameAttemptsClimb = this.endgame_attempts_climb,
        endgameClimbSuccess = this.endgame_climb_success,
        endgameClimbPosition = this.endgame_climb_position,

        // Teleop
        teleopFuelCount = this.teleop_fuel_count,
        teleopFlag = this.teleop_flag,
        teleopCompleted = this.teleop_completed,

        // Postgame
        postgameShootAnywhere = this.postgame_shoot_anywhere,
        postgameShootWhileMoving = this.postgame_shoot_while_moving,
        postgameStockpileNeutral = this.postgame_stockpile_neutral,
        postgameStockpileAlliance = this.postgame_stockpile_alliance,
        postgameStockpileCrossCourt = this.postgame_stockpile_cross_court,
        postgameFeedOutpost = this.postgame_feed_outpost,
        postgameReceiveOutpost = this.postgame_receive_outpost,
        postgameUnderTrench = this.postgame_under_trench,
        postgameOverBump = this.postgame_over_bump,
        postgameFlag = this.postgame_flag,
        )
}

data class GameDetailsActions(
    val frc_season_master_sm_year: Int,
    val competition_master_cm_event_code: String,
    val game_matchup_gm_game_type: Char,
    val game_matchup_gm_number: Int,
    val game_matchup_gm_alliance: Char,
    val game_matchup_gm_alliance_position: Int,
    val game_element_group_geg_grp_key: Int,
    val game_element_ge_key: Int,
    val gd_value: Int,
    val gd_score: Int,
    val gd_um_id: String,
    val gd_auton_path : String
)

data class Decuple<A, B, C, D, E, F, G, H, I, J>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J
)
val gameElementDefinitions = arrayOf(
    Decuple(2026, 1, 1001, "Starting Location", 1, "float", 0, 100, "", "% based on user selection"),
    Decuple(2026, 1, 1002, "Robot is on the field", 2, "int", 0, 1, "", "0 = No 1 = Yes"),
    Decuple(2026, 1, 1003, "Robot is preloaded", 3, "int", 0, 1, "", "0 = No 1 = Yes"),

    Decuple(2026, 2, 2001, "AutonPath", 1, "string", 0, 0, "", ""),
    Decuple(2026, 2, 2101, "Robot attempts climb in Auton", 2, "int", 0, 1, "", "0 = No 1 = Yes"),
    Decuple(2026, 2, 2103, "Robot climbs successful in Auton", 3, "int", 0, 1, "", "0 = No 1 = Yes"),
    Decuple(2026, 2, 2104, "Robot climb position in Auton", 4, "string", 0, 0, "", ""),
    Decuple(2026, 2, 2201, "Auton Fuel Count", 5, "int", 0, 0, "", "This is the calculated auton fuel count shot by the team"),

    Decuple(2026, 3, 3001, "Transition Shift - Cycling Time", 1, "int", 0, 0, "", "Time in seconds"),
    Decuple(2026, 3, 3002, "Transition Shift - Stockpiling Time", 2, "int", 0, 0, "", "Time in seconds"),
    Decuple(2026, 3, 3003, "Transition Shift - Defending Time", 3, "int", 0, 0, "", "Time in seconds"),
    Decuple(2026, 3, 3004, "Transition Shift - Broken Time", 4, "int", 0, 0, "", "Time in seconds"),
    Decuple(2026, 3, 3000, "1st Active", 5, "int", 0, 1, "", "0 = No 1 = Yes"),

    Decuple(2026, 3, 3101, "1st Shift - Cycling Time", 6, "int", 0, 0, "", "Time in seconds"),
    Decuple(2026, 3, 3102, "1st Shift - Stockpiling Time", 7, "int", 0, 0, "", "Time in seconds"),
    Decuple(2026, 3, 3103, "1st Shift - Defending Time", 8, "int", 0, 0, "", "Time in seconds"),
    Decuple(2026, 3, 3104, "1st Shift - Broken Time", 9, "int", 0, 0, "", "Time in seconds"),

    Decuple(2026, 3, 3201, "2nd Shift - Cycling Time", 10, "int", 0, 0, "", "Time in seconds"),
    Decuple(2026, 3, 3202, "2nd Shift - Stockpiling Time", 11, "int", 0, 0, "", "Time in seconds"),
    Decuple(2026, 3, 3203, "2nd Shift - Defending Time", 12, "int", 0, 0, "", "Time in seconds"),
    Decuple(2026, 3, 3204, "2nd Shift - Broken Time", 13, "int", 0, 0, "", "Time in seconds"),

    Decuple(2026, 3, 3301, "3rd Shift - Cycling Time", 14, "int", 0, 0, "", "Time in seconds"),
    Decuple(2026, 3, 3302, "3rd Shift - Stockpiling Time", 15, "int", 0, 0, "", "Time in seconds"),
    Decuple(2026, 3, 3303, "3rd Shift - Defending Time", 16, "int", 0, 0, "", "Time in seconds"),
    Decuple(2026, 3, 3304, "3rd Shift - Broken Time", 17, "int", 0, 0, "", "Time in seconds"),

    Decuple(2026, 3, 3401, "4th Shift - Cycling Time", 18, "int", 0, 0, "", "Time in seconds"),
    Decuple(2026, 3, 3402, "4th Shift - Stockpiling Time", 19, "int", 0, 0, "", "Time in seconds"),
    Decuple(2026, 3, 3403, "4th Shift - Defending Time", 20, "int", 0, 0, "", "Time in seconds"),
    Decuple(2026, 3, 3404, "4th Shift - Broken Time", 21, "int", 0, 0, "", "Time in seconds"),

    Decuple(2026, 3, 3501, "End Game - Cycling Time", 22, "int", 0, 0, "", "Time in seconds"),
    Decuple(2026, 3, 3502, "End Game - Stockpiling Time", 23, "int", 0, 0, "", "Time in seconds"),
    Decuple(2026, 3, 3503, "End Game - Defending Time", 24, "int", 0, 0, "", "Time in seconds"),
    Decuple(2026, 3, 3504, "End Game - Broken Time", 25, "int", 0, 0, "", "Time in seconds"),

    Decuple(2026, 4, 4101, "Robot attempts climb in Endgame", 2, "int", 0, 1, "", "0 = No 1 = Yes"),
    Decuple(2026, 4, 4102, "Robot climbs successful in Endgame", 3, "int", 0, 1, "", "0 = No 1 = Yes"),
    Decuple(2026, 4, 4103, "Robot climb position in Endgame", 4, "string", 0, 0, "", ""),

    Decuple(2026, 4, 4200, "Teleop Fuel Count", 1, "int", 0, 0, "", "This is the calculated auton fuel count shot by the team"),

    Decuple(2026, 4, 4201, "Robot shoot from anywhere", 1, "int", 0, 1, "", "0 = No, 1 - Yes"),
    Decuple(2026, 4, 4202, "Robot shoot while moving", 1, "int", 0, 1, "", "0 = No, 1 - Yes"),
    Decuple(2026, 4, 4203, "Robot stockpiling from neutral zone", 1, "int", 0, 1, "", "0 = No, 1 - Yes"),
    Decuple(2026, 4, 4204, "Robot stockpiling from alliance zone", 1, "int", 0, 1, "", "0 = No, 1 - Yes"),
    Decuple(2026, 4, 4205, "Robot stockpiling from cross court", 1, "int", 0, 1, "", "0 = No, 1 - Yes"),
    Decuple(2026, 4, 4206, "Robot feed fuel to outpost", 1, "int", 0, 1, "", "0 = No, 1 - Yes"),
    Decuple(2026, 4, 4207, "Robot receive fuel from outpost", 1, "int", 0, 1, "", "0 = No, 1 - Yes"),
    Decuple(2026, 4, 4208, "Robot drive under trench", 1, "int", 0, 1, "", "0 = No, 1 - Yes"),
    Decuple(2026, 4, 4209, "Robot go over trench", 1, "int", 0, 1, "", "0 = No, 1 - Yes"),
    Decuple(2026, 4, 4210, "Review match flag", 1, "int", 0, 1, "", "0 = No, 1 - Yes")
)

fun GameDetailsEntity.convertToList(gameConstants: GameConstants,game_matchup_gm_game_type: Char, game_matchup_gm_number:Int,game_matchup_gm_alliance:Char,game_matchup_gm_alliance_position: Int, user: String): List<GameDetailsActions> {
    fun calculateScore(ge: Int, gdv: Int): Int {
        return when {
            // leave start area during auton
            ge == 2103 && gdv == 1 -> 15

            // auton fuel
            ge == 2201 && gdv != 0 -> gdv * 1

            // teleop fuel
            ge == 4200 && gdv != 0 -> gdv * 1

            // climb scoring
            ge == 4103 && gdv == 1 -> 10
            ge == 4103 && gdv == 2 -> 20
            ge == 4103 && gdv == 3 -> 30

            // default
            else -> 0
        }
    }


    val list = mutableListOf<GameDetailsActions>()
    var index = 0

    for (prop in this::class.memberProperties) {
        if (index >= gameElementDefinitions.size) break

        val value = (prop as KProperty1<GameDetailsEntity, *>).get(this)
        val def = gameElementDefinitions[index]

        list.add(
            GameDetailsActions(
                frc_season_master_sm_year= gameConstants.frc_season_master_sm_year,
                competition_master_cm_event_code= gameConstants.competition_master_cm_event_code,
                game_matchup_gm_game_type=game_matchup_gm_game_type,
                game_matchup_gm_number= game_matchup_gm_number,
                game_matchup_gm_alliance= game_matchup_gm_alliance,//
                game_matchup_gm_alliance_position= game_matchup_gm_alliance_position,//
                game_element_group_geg_grp_key= def.second,
                game_element_ge_key=def.third,
                gd_value = value?.toString()?.toIntOrNull() ?: 0,
                gd_score= calculateScore(def.third,value?.toString()?.toIntOrNull() ?: 0),
                gd_um_id= user,
                gd_auton_path = ""
            )
        )

        index++
    }

    return list
}
