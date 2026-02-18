package com.team695.scoutifyapp.data.api.model

import com.team695.scoutifyapp.db.GameDetailsEntity

data class GameDetails(
    val id: Long = 0,

    // Starting & Preload
    val startingLocation: Double? = null,
    val robotOnField: Boolean? = null,
    val robotPreloaded: Boolean? = null,

    // Auton
    val autonPath: String? = null,
    val autonAttemptsClimb: Boolean? = null,
    val autonClimbSuccess: Boolean? = null,
    val autonClimbPosition: String? = null,
    val autonFuelCount: Int? = null,

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

    // Teleop
    val teleopFuelCount: Int? = null,
    val teleopShootAnywhere: Boolean? = null,
    val teleopShootWhileMoving: Boolean? = null,
    val teleopStockpileNeutral: Boolean? = null,
    val teleopStockpileAlliance: Boolean? = null,
    val teleopStockpileCrossCourt: Boolean? = null,
    val teleopFeedOutpost: Boolean? = null,
    val teleopReceiveOutpost: Boolean? = null,
    val teleopUnderTrench: Boolean? = null,
    val teleopOverTrench: Boolean? = null,

    // Review
    val reviewMatchFlag: Boolean? = null
)


fun GameDetailsEntity.createGameDetailsFromDb(): GameDetails {
    return GameDetails(
        id = this.id,

        // Starting & Preload
        startingLocation = this.starting_location,
        robotOnField = this.robot_on_field,
        robotPreloaded = this.robot_preloaded,

        // Auton
        autonPath = this.auton_path,
        autonAttemptsClimb = this.auton_attempts_climb,
        autonClimbSuccess = this.auton_climb_success,
        autonClimbPosition = this.auton_climb_position,
        autonFuelCount = this.auton_fuel_count,

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

        endgameAttemptsClimb = this.endgame_attempts_climb,
        endgameClimbSuccess = this.endgame_climb_success,
        endgameClimbPosition = this.endgame_climb_position,

        // Teleop
        teleopFuelCount = this.teleop_fuel_count,
        teleopShootAnywhere = this.teleop_shoot_anywhere,
        teleopShootWhileMoving = this.teleop_shoot_while_moving,
        teleopStockpileNeutral = this.teleop_stockpile_neutral,
        teleopStockpileAlliance = this.teleop_stockpile_alliance,
        teleopStockpileCrossCourt = this.teleop_stockpile_cross_court,
        teleopFeedOutpost = this.teleop_feed_outpost,
        teleopReceiveOutpost = this.teleop_receive_outpost,
        teleopUnderTrench = this.teleop_under_trench,
        teleopOverTrench = this.teleop_over_trench,

        // Review
        reviewMatchFlag = this.review_match_flag
    )
}

fun GameDetails.toDbEntity(): GameDetailsEntity {
    return GameDetailsEntity(
        id = this.id,

        // Starting & Preload
        starting_location = this.startingLocation,
        robot_on_field = this.robotOnField,
        robot_preloaded = this.robotPreloaded,

        // Auton
        auton_path = this.autonPath,
        auton_attempts_climb = this.autonAttemptsClimb,
        auton_climb_success = this.autonClimbSuccess,
        auton_climb_position = this.autonClimbPosition,
        auton_fuel_count = this.autonFuelCount,

        // Transition Shift
        transition_cycling_time = this.transitionCyclingTime,
        transition_stockpiling_time = this.transitionStockpilingTime,
        transition_defending_time = this.transitionDefendingTime,
        transition_broken_time = this.transitionBrokenTime,
        transition_first_active = this.transitionFirstActive,

        // 1st Shift
        shift1_cycling_time = this.shift1CyclingTime,
        shift1_stockpiling_time = this.shift1StockpilingTime,
        shift1_defending_time = this.shift1DefendingTime,
        shift1_broken_time = this.shift1BrokenTime,

        // 2nd Shift
        shift2_cycling_time = this.shift2CyclingTime,
        shift2_stockpiling_time = this.shift2StockpilingTime,
        shift2_defending_time = this.shift2DefendingTime,
        shift2_broken_time = this.shift2BrokenTime,

        // 3rd Shift
        shift3_cycling_time = this.shift3CyclingTime,
        shift3_stockpiling_time = this.shift3StockpilingTime,
        shift3_defending_time = this.shift3DefendingTime,
        shift3_broken_time = this.shift3BrokenTime,

        // 4th Shift
        shift4_cycling_time = this.shift4CyclingTime,
        shift4_stockpiling_time = this.shift4StockpilingTime,
        shift4_defending_time = this.shift4DefendingTime,
        shift4_broken_time = this.shift4BrokenTime,

        // Endgame
        endgame_cycling_time = this.endgameCyclingTime,
        endgame_stockpiling_time = this.endgameStockpilingTime,
        endgame_defending_time = this.endgameDefendingTime,
        endgame_broken_time = this.endgameBrokenTime,
        endgame_attempts_climb = this.endgameAttemptsClimb,
        endgame_climb_success = this.endgameClimbSuccess,
        endgame_climb_position = this.endgameClimbPosition,

        // Teleop
        teleop_fuel_count = this.teleopFuelCount,
        teleop_shoot_anywhere = this.teleopShootAnywhere,
        teleop_shoot_while_moving = this.teleopShootWhileMoving,
        teleop_stockpile_neutral = this.teleopStockpileNeutral,
        teleop_stockpile_alliance = this.teleopStockpileAlliance,
        teleop_stockpile_cross_court = this.teleopStockpileCrossCourt,
        teleop_feed_outpost = this.teleopFeedOutpost,
        teleop_receive_outpost = this.teleopReceiveOutpost,
        teleop_under_trench = this.teleopUnderTrench,
        teleop_over_trench = this.teleopOverTrench,

        // Review
        review_match_flag = this.reviewMatchFlag
    )
}