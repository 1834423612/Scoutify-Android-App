package com.team695.scoutifyapp.data.api.model
import android.util.Log
import com.team695.scoutifyapp.db.GameDetailsEntity
import kotlin.Boolean
import kotlin.Double
import kotlin.jvm.Transient
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties


data class GameDetails(
    val id: Int? = null,
    val task_id: Int? = null,

    val matchNumber: Int? = null,
    val alliance: Char? = null,
    val alliancePosition: Int? = null,
    val startingLocation: Double? = null,
    val robotOnField: Boolean? = null,
    val robotPreloaded: Boolean? = null,
    val pregameFlag: Boolean? = null,
    val autonPath: String? = null,
    val autonAttemptsClimb: Boolean? = null,
    val autonClimbSuccess: Boolean? = null,
    val autonClimbPosition: String? = null,
    val autonFuelCount: Int? = null,
    val autonFlag: Boolean? = null,
    val transitionCyclingTime: Int? = null,
    val transitionStockpilingTime: Int? = null,
    val transitionDefendingTime: Int? = null,
    val transitionBrokenTime: Int? = null,
    val transitionFirstActive: Boolean? = null,
    val shift1CyclingTime: Int? = null,
    val shift1StockpilingTime: Int? = null,
    val shift1DefendingTime: Int? = null,
    val shift1BrokenTime: Int? = null,
    val shift2CyclingTime: Int? = null,
    val shift2StockpilingTime: Int? = null,
    val shift2DefendingTime: Int? = null,
    val shift2BrokenTime: Int? = null,
    val shift3CyclingTime: Int? = null,
    val shift3StockpilingTime: Int? = null,
    val shift3DefendingTime: Int? = null,
    val shift3BrokenTime: Int? = null,
    val shift4CyclingTime: Int? = null,
    val shift4StockpilingTime: Int? = null,
    val shift4DefendingTime: Int? = null,
    val shift4BrokenTime: Int? = null,
    val endgameCyclingTime: Int? = null,
    val endgameStockpilingTime: Int? = null,
    val endgameDefendingTime: Int? = null,
    val endgameBrokenTime: Int? = null,
    val endgameAttemptsClimb: Boolean? = null,
    val endgameClimbSuccess: Boolean? = null,
    val endgameClimbPosition: String? = null,
    val endgameClimbCode: String? = null,
    val teleopFuelCount: Int? = null,
    val teleopFlag: Boolean? = null,
    val teleopCompleted: Boolean? = null,
    @Transient val localTeleopSection: String? = null,
    @Transient val localTeleopTotalMilliseconds: Int? = null,
    @Transient val localTeleopCachedMilliseconds: Int? = null,
    val postgameShootWhileMoving: Boolean? = null,
    val postgameStockpileNeutral: Boolean? = null,
    val postgameStockpileCrossCourt: Boolean? = null,
    val postgameFeedOutpost: Boolean? = null,
    val postgameReceiveOutpost: Boolean? = null,
    val postgameUnderTrench: Boolean? = null,
    val postgameOverBump: Boolean? = null,
    val postgameShootAnywhere: Boolean? = null,
    val postgameFlag: Boolean? = null,
    val reviewMatchFlag: Boolean? = null
) {
    val endgameClimbSuccessFilled: Boolean get() {
        if(endgameAttemptsClimb == true) {
            return endgameClimbSuccess == true
        }
        return true
    }
    val endgameClimbPositionFilled: Boolean get() {
        val isEmpty: Boolean = endgameClimbCode.isNullOrEmpty()
        return when(endgameClimbSuccess) {
            true -> !isEmpty
            false -> isEmpty
            null -> false
        }
    }

    val pregameProgress: Float get() {
        val pregameVars = listOf<Any?>(robotOnField, robotPreloaded)
        return pregameVars.count { it != null }.toFloat() / pregameVars.size
    }

    val endgameProgress: Float get() {
        val endgameVars = listOf<Any?>(endgameAttemptsClimb, endgameClimbSuccess)
        val filledElements: Int = endgameVars.count { it != null } + if(endgameClimbPositionFilled) 1 else 0
        val totalElements: Int = endgameVars.size + 1
        return filledElements.toFloat() / totalElements
    }

    val postgameProgress: Float get() {
        val postgameVars = listOf<Any?>(
            postgameShootWhileMoving,
            postgameStockpileNeutral,
            postgameStockpileCrossCourt,
            postgameFeedOutpost,
            postgameReceiveOutpost,
            postgameUnderTrench,
            postgameOverBump,
            postgameShootAnywhere,
        )
        return postgameVars.count { it != null }.toFloat() / postgameVars.size
    }
}

fun GameDetailsEntity.createGameDetailsFromDb(): GameDetails {
    return GameDetails(
        id = this.id,
        task_id = this.task_id,
        alliance = this.alliance,
        alliancePosition = this.alliance_position,
        matchNumber = this.match_number,
        startingLocation = this.starting_location,
        robotOnField = this.robot_on_field,
        robotPreloaded = this.robot_preloaded,
        pregameFlag = this.pregame_flag,
        autonPath = this.auton_path,
        autonAttemptsClimb = this.auton_attempts_climb,
        autonClimbSuccess = this.auton_climb_success,
        autonClimbPosition = this.auton_climb_position,
        autonFuelCount = this.auton_fuel_count,
        autonFlag = this.auton_flag,
        transitionCyclingTime = this.transition_cycling_time,
        transitionStockpilingTime = this.transition_stockpiling_time,
        transitionDefendingTime = this.transition_defending_time,
        transitionBrokenTime = this.transition_broken_time,
        transitionFirstActive = this.transition_first_active,
        shift1CyclingTime = this.shift1_cycling_time,
        shift1StockpilingTime = this.shift1_stockpiling_time,
        shift1DefendingTime = this.shift1_defending_time,
        shift1BrokenTime = this.shift1_broken_time,
        shift2CyclingTime = this.shift2_cycling_time,
        shift2StockpilingTime = this.shift2_stockpiling_time,
        shift2DefendingTime = this.shift2_defending_time,
        shift2BrokenTime = this.shift2_broken_time,
        shift3CyclingTime = this.shift3_cycling_time,
        shift3StockpilingTime = this.shift3_stockpiling_time,
        shift3DefendingTime = this.shift3_defending_time,
        shift3BrokenTime = this.shift3_broken_time,
        shift4CyclingTime = this.shift4_cycling_time,
        shift4StockpilingTime = this.shift4_stockpiling_time,
        shift4DefendingTime = this.shift4_defending_time,
        shift4BrokenTime = this.shift4_broken_time,
        endgameCyclingTime = this.endgame_cycling_time,
        endgameStockpilingTime = this.endgame_stockpiling_time,
        endgameDefendingTime = this.endgame_defending_time,
        endgameBrokenTime = this.endgame_broken_time,
        endgameClimbCode = this.endgame_climb_code,
        endgameAttemptsClimb = this.endgame_attempts_climb,
        endgameClimbSuccess = this.endgame_climb_success,
        endgameClimbPosition = this.endgame_climb_position,
        teleopFuelCount = this.teleop_fuel_count,
        teleopFlag = this.teleop_flag,
        teleopCompleted = this.teleop_completed,
        localTeleopSection = this.local_teleop_section,
        localTeleopTotalMilliseconds = this.local_teleop_total_milliseconds,
        localTeleopCachedMilliseconds = this.local_teleop_cached_milliseconds,
        postgameShootAnywhere = this.postgame_shoot_anywhere,
        postgameShootWhileMoving = this.postgame_shoot_while_moving,
        postgameStockpileNeutral = this.postgame_stockpile_neutral,
        postgameStockpileCrossCourt = this.postgame_stockpile_cross_court,
        postgameFeedOutpost = this.postgame_feed_outpost,
        postgameReceiveOutpost = this.postgame_receive_outpost,
        postgameUnderTrench = this.postgame_under_trench,
        postgameOverBump = this.postgame_over_bump,
        postgameFlag = this.postgame_flag,
    )
}
