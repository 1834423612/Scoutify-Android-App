package com.team695.scoutifyapp.data.repository

import android.util.Log
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.team695.scoutifyapp.data.api.client.ScoutifyClient
import com.team695.scoutifyapp.data.api.model.GameConstants
import com.team695.scoutifyapp.data.api.model.GameConstantsStore
import com.team695.scoutifyapp.data.api.model.GameDetails
import com.team695.scoutifyapp.data.api.model.GameDetailsActions
import com.team695.scoutifyapp.data.api.model.Task
import com.team695.scoutifyapp.data.api.model.convertToList
import com.team695.scoutifyapp.data.api.model.createGameDetailsFromDb
import com.team695.scoutifyapp.data.api.model.createTaskFromDb
import com.team695.scoutifyapp.data.api.service.ApiResponse
import com.team695.scoutifyapp.data.api.service.GameDetailsService
import com.team695.scoutifyapp.db.AppDatabase
import com.team695.scoutifyapp.db.GameDetailsEntity
import com.team695.scoutifyapp.db.MatchEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


class GameDetailRepository(
    private val service: GameDetailsService,
    private val db: AppDatabase,
) {

    val isReady: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var pulledConstants = false
        private set

    suspend fun getGameDetailsByTaskId(taskId: Int): GameDetails {
        return withContext(Dispatchers.IO) {
            val gameEntityList: List<GameDetailsEntity> = db.gameDetailsQueries
                .selectDetailsForTask(taskId)
                .executeAsList()

            if (gameEntityList.isNotEmpty()) {
                return@withContext gameEntityList[0].createGameDetailsFromDb()
            }

            val newDetails = GameDetails(task_id = taskId)
            updateDbFromGameDetails(newDetails)
            return@withContext newDetails
        }
    }

    suspend fun pushGameDetails(): Result<List<GameDetailsActions>> {
        fun findTeamField(match: MatchEntity, team: Long): String? {
            return when (team) {
                match.r1.toLong() -> "r1"
                match.r2.toLong() -> "r2"
                match.r3.toLong() -> "r3"
                match.b1.toLong() -> "b1"
                match.b2.toLong() -> "b2"
                match.b3.toLong() -> "b3"
                else -> null
            }
        }

        return withContext(Dispatchers.IO) {
            try {
                val gameDetails = db.gameDetailsQueries.selectAllGameDetails().executeAsList()

                val gameDetailsConverted = mutableListOf<GameDetailsActions>()

                for (i in gameDetails) {

                    val gameConstants = GameConstantsStore.constants   // non-null

                    val task = db.taskQueries.selectTaskById(i.task_id.toLong()).executeAsOne()
                    val matchNumber = task.matchNum
                    val teamNumber = task.teamNum.toLong()

                    val match = db.matchQueries
                        .selectMatchByNumber(matchNumber )
                        .executeAsOne()

                    val field = findTeamField(match, teamNumber)

                    val alliance: Char = field?.first() ?: ' '          // Char
                    val alliancePosition: Int = field?.last()?.digitToInt() ?: 0

                    val user: String = db.userQueries.selectUser().executeAsOne().name ?: ""

                    gameDetailsConverted.addAll(
                        i.convertToList(
                            gameConstants,
                            match.gameType[0],
                            matchNumber.toInt(),
                            alliance,
                            alliancePosition,
                            user
                        )
                    )
                }

                service.updateGameDetails(
                    acToken = ScoutifyClient.tokenManager.getToken()!!,
                    gameDetails = gameDetailsConverted
                )

                return@withContext Result.success(gameDetailsConverted)
            } catch (e: Exception) {
                Log.d("Game details", "Error when trying to push game details: $e")
                return@withContext Result.failure(e)
            }
        }
    }

    suspend fun updateDbFromGameDetails(details: GameDetails) {
        withContext(Dispatchers.IO) {
            db.gameDetailsQueries.insertDetails(
                task_id = details.task_id!!,

                // Starting
                starting_location = details.startingLocation,
                robot_on_field = details.robotOnField,
                robot_preloaded = details.robotPreloaded,
                pregame_flag = details.pregameFlag,

                // Auton
                auton_path = details.autonPath,
                auton_attempts_climb = details.autonAttemptsClimb,
                auton_climb_success = details.autonClimbSuccess,
                auton_climb_position = details.autonClimbPosition,
                auton_fuel_count = details.autonFuelCount,
                auton_flag = details.autonFlag,

                // Transition
                transition_cycling_time = details.transitionCyclingTime,
                transition_stockpiling_time = details.transitionStockpilingTime,
                transition_defending_time = details.transitionDefendingTime,
                transition_broken_time = details.transitionBrokenTime,
                transition_first_active = details.transitionFirstActive,

                // Shifts (1-4)
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

                // Endgame
                endgame_cycling_time = details.endgameCyclingTime,
                endgame_stockpiling_time = details.endgameStockpilingTime,
                endgame_defending_time = details.endgameDefendingTime,
                endgame_broken_time = details.endgameBrokenTime,
                endgame_attempts_climb = details.endgameAttemptsClimb,
                endgame_climb_success = details.endgameClimbSuccess,
                endgame_climb_position = details.endgameClimbPosition,
                endgame_climb_code = details.endgameClimbCode,

                teleop_fuel_count = details.teleopFuelCount,
                teleop_flag = details.teleopFlag,
                teleop_completed = details.teleopCompleted,

                // Postgame
                postgame_shoot_anywhere = details.postgameShootAnywhere,
                postgame_shoot_while_moving = details.postgameShootWhileMoving,
                postgame_stockpile_neutral = details.postgameStockpileNeutral,
                postgame_stockpile_alliance = details.postgameStockpileAlliance,
                postgame_stockpile_cross_court = details.postgameStockpileCrossCourt,
                postgame_feed_outpost = details.postgameFeedOutpost,
                postgame_receive_outpost = details.postgameReceiveOutpost,
                postgame_under_trench = details.postgameUnderTrench,
                postgame_over_bump = details.postgameOverBump,
                postgame_flag = details.postgameFlag,
            )

            pushGameDetails()
        }
    }

    suspend fun setGameConstants(): Result<GameConstants> {
        if (pulledConstants)
            return Result.failure(Exception("already pulled game constants"))

        pulledConstants = true

        return withContext(Dispatchers.IO) {
            try {
                val result: ApiResponse<GameConstants> = service.getGameConstants(
                    acToken = ScoutifyClient.tokenManager.getToken()!!
                )

                if (result.data != null) {
                    if (result.data != GameConstantsStore.constants) {
                        db.transaction {
                            db.taskQueries.clearAllTasks()
                            db.matchQueries.clearAllMatches()
                        }
                    }

                    GameConstantsStore.set(result.data)

                    db.gameConstantsQueries.insertOrUpdateConstants(
                        frc_season_master_sm_year = result.data.frc_season_master_sm_year,
                        competition_master_cm_event_code = result.data.competition_master_cm_event_code,
                        game_matchup_gm_game_type = result.data.game_matchup_gm_game_type
                    )

                    isReady.value = true

                    return@withContext Result.success(result.data)
                } else {
                    pulledConstants = false
                    return@withContext Result.failure(Exception("Game constants are empty"))
                }
            } catch (e: Exception) {
                pulledConstants = false
                isReady.value = true
                Log.e("Game Constants", "Error when trying to fetch gameConstants: $e")
                return@withContext Result.failure(e)
            }
        }
    }

    /*suspend fun fetchGameDetails(): Result<List<Match>> {

    }*/
}