package com.team695.scoutifyapp.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.team695.scoutifyapp.data.api.model.GameDetails
import com.team695.scoutifyapp.data.api.model.createGameDetailsFromDb
import com.team695.scoutifyapp.data.api.service.GameDetailsService
import com.team695.scoutifyapp.db.AppDatabase
import com.team695.scoutifyapp.db.GameDetailsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class GameDetailRepository(
    private val service: GameDetailsService,
    private val db: AppDatabase,
) {
    /*val gameDetails: Flow<List<GameDetailsEntity>> = db.gameDetailsQueries.selectAllGameDetails()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { entries ->
            entries.map { entity ->
                entity.createGameDetailsFromDb()
            }
        } */

    suspend fun updateDbFromGameDetails(details: GameDetails) {
        db.gameDetailsQueries.insertDetail(
            task_id = details.task_id!!,

            // 🏁 Starting
            starting_location = details.startingLocation,
            robot_on_field = details.robotOnField,
            robot_preloaded = details.robotPreloaded,

            // 🤖 Auton
            auton_path = details.autonPath,
            auton_attempts_climb = details.autonAttemptsClimb,
            auton_climb_success = details.autonClimbSuccess,
            auton_climb_position = details.autonClimbPosition,
            auton_fuel_count = details.autonFuelCount,

            // 🔄 Transition
            transition_cycling_time = details.transitionCyclingTime,
            transition_stockpiling_time = details.transitionStockpilingTime,
            transition_defending_time = details.transitionDefendingTime,
            transition_broken_time = details.transitionBrokenTime,
            transition_first_active = details.transitionFirstActive,

            // 🕒 Shifts (1-4)
            shift1_cycling_time = details.shift1CyclingTime,
            shift1_stockpiling_time = details.shift1StockpilingTime,
            shift1_defending_time = details.shift1DefendingTime,
            shift1_broken_time = details.shift1BrokenTime,

            shift2_cycling_time = details.shift2CyclingTime,
            shift2_stockpiling_time = details.shift2StockpilingTime,
            shift2_defending_time = details.shift2DefendingTime,
            shift2_broken_time = details.shift2BrokenTime,

            shift3_cycling_time = details.shift3CyclingTime,
            shift3_stockpiling_time = details.shift3StockpilingTime,
            shift3_defending_time = details.shift3DefendingTime,
            shift3_broken_time = details.shift3BrokenTime,

            shift4_cycling_time = details.shift4CyclingTime,
            shift4_stockpiling_time = details.shift4StockpilingTime,
            shift4_defending_time = details.shift4DefendingTime,
            shift4_broken_time = details.shift4BrokenTime,

            // 🏁 Endgame
            endgame_cycling_time = details.endgameCyclingTime,
            endgame_stockpiling_time = details.endgameStockpilingTime,
            endgame_defending_time = details.endgameDefendingTime,
            endgame_broken_time = details.endgameBrokenTime,
            endgame_attempts_climb = details.endgameAttemptsClimb,
            endgame_climb_success = details.endgameClimbSuccess,
            endgame_climb_position = details.endgameClimbPosition,

            // 🎮 Teleop Flags
            teleop_fuel_count = details.teleopFuelCount,
            teleop_shoot_anywhere = details.teleopShootAnywhere,
            teleop_shoot_while_moving = details.teleopShootWhileMoving,
            teleop_stockpile_neutral = details.teleopStockpileNeutral,
            teleop_stockpile_alliance = details.teleopStockpileAlliance,
            teleop_stockpile_cross_court = details.teleopStockpileCrossCourt,
            teleop_feed_outpost = details.teleopFeedOutpost,
            teleop_receive_outpost = details.teleopReceiveOutpost,
            teleop_under_trench = details.teleopUnderTrench,
            teleop_over_trench = details.teleopOverTrench,

            // ✅ Review
            review_match_flag = details.reviewMatchFlag
        )
    }

    /*suspend fun fetchGameDetails(): Result<List<Match>> {

    }*/
}