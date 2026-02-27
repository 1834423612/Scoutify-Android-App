package com.team695.scoutifyapp.data.api.model

import com.team695.scoutifyapp.db.GameDetailsEntity
import com.team695.scoutifyapp.ui.viewModels.Stroke
import kotlin.Boolean
import kotlin.Double

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
    val pregameProgress: Float get() {
        val pregameVars = listOf<Any?>(
            //startingLocation, //TO DO: add this
            robotOnField,
            robotPreloaded
        )
        return pregameVars.count( {it != null} ).toFloat() / pregameVars.size

    }

    val autonProgress: Float get() {
        //TO DO: add this
        /*val autonVars = listOf<Any?>(
            autonPath,
            autonAttemptsClimb,
            autonClimbSuccess,
            autonClimbPosition
        )
        return autonVars.count( {it != null} ).toFloat() / autonVars.size */
        return 1f
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
            postgameFlag,
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
