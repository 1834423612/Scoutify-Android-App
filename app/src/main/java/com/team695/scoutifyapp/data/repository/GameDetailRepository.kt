package com.team695.scoutifyapp.data.repository

import android.content.Context
import android.util.Log
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.team695.scoutifyapp.BuildConfig
import com.team695.scoutifyapp.data.api.client.ScoutifyClient
import com.team695.scoutifyapp.data.api.model.Asset
import com.team695.scoutifyapp.data.api.model.GameConstants
import com.team695.scoutifyapp.data.api.model.GameConstantsStore
import com.team695.scoutifyapp.data.api.model.GameDetails
import com.team695.scoutifyapp.data.api.model.GithubResponse
import com.team695.scoutifyapp.data.api.model.Task
import com.team695.scoutifyapp.data.api.model.createGameDetailsFromDb
import com.team695.scoutifyapp.data.api.model.createTaskFromDb
import com.team695.scoutifyapp.data.api.service.ApiResponse
import com.team695.scoutifyapp.data.api.service.AppService
import com.team695.scoutifyapp.data.api.service.GameDetailsService
import com.team695.scoutifyapp.data.update.UpdateManager
import com.team695.scoutifyapp.db.AppDatabase
import com.team695.scoutifyapp.db.GameDetailsEntity
import com.team695.scoutifyapp.db.MatchEntity
import com.team695.scoutifyapp.db.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.text.toLong


class GameDetailRepository(
    private val gameDetailsService: GameDetailsService,
    private val appService: AppService,
    private val db: AppDatabase,
): Repository {

    val isReady: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var pulledConstants = false
        private set

    override suspend fun push(): Result<List<GameDetails>> {
        return withContext(Dispatchers.IO) {
            try {
                val gameDetails = db.gameDetailsQueries
                    .selectCompletedTasks()
                    .executeAsList()
                    .map { entity ->
                        entity.createGameDetailsFromDb()
                    }

                if (gameDetails.isNotEmpty()) {
                    gameDetailsService.updateGameDetails(
                        acToken = ScoutifyClient.tokenManager.getToken()!!,
                        gameDetails = gameDetails
                    )

                    Log.d("Game Details", "Pushed game details successfully")
                }  else {
                    Log.d("Game Details", "No game details to push")
                }

                return@withContext Result.success(gameDetails)
            } catch (e: Exception) {
                Log.d("Game details", "Error when trying to push game details: $e")
                return@withContext Result.failure(e)
            }
        }
    }

    suspend fun getGameDetailsByTaskId(taskId: Int): GameDetails {
        return withContext(Dispatchers.IO) {
            val gameEntityList: List<GameDetailsEntity> = db.gameDetailsQueries
                .selectDetailsForTask(taskId)
                .executeAsList()

            if (gameEntityList.isNotEmpty()) {
                return@withContext gameEntityList[0].createGameDetailsFromDb()
            }

            val taskEntity: TaskEntity? = db.taskQueries
                .selectTaskById(taskId.toLong())
                .executeAsOneOrNull()

            if (taskEntity == null) {
                throw Error("Could not find task_id $taskId for game details")
            }

            val teamInfo = getTeamInfo(taskEntity)
            Log.d("GAME_DETAIL_REPOSITORY", teamInfo.first.toString() + teamInfo.second)

            val newDetails = GameDetails(
                task_id = taskId,
                matchNumber = teamInfo.first,
                alliance = teamInfo.second!![0].uppercaseChar(),
                alliancePosition = teamInfo.second!![1].digitToInt()
            )

            updateDbFromGameDetails(newDetails)
            return@withContext newDetails
        }
    }


    suspend fun updateDbFromGameDetails(details: GameDetails) {
        withContext(Dispatchers.IO) {
            db.gameDetailsQueries.insertDetails(
                task_id = details.task_id!!,
                alliance = details.alliance!!.uppercaseChar(),
                alliance_position = details.alliancePosition!!,
                match_number = details.matchNumber!!,

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
                postgame_stockpile_cross_court = details.postgameStockpileCrossCourt,
                postgame_feed_outpost = details.postgameFeedOutpost,
                postgame_receive_outpost = details.postgameReceiveOutpost,
                postgame_under_trench = details.postgameUnderTrench,
                postgame_over_bump = details.postgameOverBump,
                postgame_flag = details.postgameFlag,
            )
        }
    }

    override suspend fun fetch(): Result<GameConstants> {
        if (pulledConstants)
            return Result.failure(Exception("already pulled game constants"))

        pulledConstants = true

        return withContext(Dispatchers.IO) {
            try {
                val result: ApiResponse<GameConstants> = gameDetailsService.getGameConstants(
                    acToken = ScoutifyClient.tokenManager.getToken()!!
                )

                if (result.data != null) {
                    if (result.data != GameConstantsStore.constants) {
                        db.transaction {
                            db.taskQueries.clearAllTasks()
                            db.matchQueries.clearAllMatches()
                        }

                        if (BuildConfig.VERSION_NAME != result.data.app_version) {
                            val githubResult: GithubResponse = appService.getAssets()
                            val link: Asset? = githubResult.assets.find {
                                it.browserDownloadUrl.takeLast(4) == ".apk"
                            }

                            if (link != null) {
                                UpdateManager.downloadUpdate(link.browserDownloadUrl)
                            } else {
                                Log.d("Game Constants", "Update url not found!")
                            }
                        }
                    }

                    GameConstantsStore.set(result.data)

                    val year = result.data.frc_season_master_sm_year
                    val event_code = result.data.competition_master_cm_event_code
                    val game_type = result.data.game_matchup_gm_game_type
                    val app_version = result.data.app_version

                    db.gameConstantsQueries.insertOrUpdateConstants(
                        frc_season_master_sm_year = year,
                        competition_master_cm_event_code = event_code,
                        game_matchup_gm_game_type = game_type,
                        app_version = app_version
                    )

                    isReady.value = true

                    return@withContext Result.success(result.data)
                } else {
                    pulledConstants = false

                    return@withContext Result.failure(
                        Exception("Game constants are empty")
                    )
                }
            } catch (e: Exception) {
                pulledConstants = false
                isReady.value = true

                Log.e("Game Constants", "Error when trying to fetch gameConstants: $e")
                return@withContext Result.failure(e)
            }
        }
    }

    private fun getTeamInfo(task: TaskEntity): Pair<Int, String?> {
        val matchNumber = task.matchNum
        val teamNumber = task.teamNum

        val match = db.matchQueries
            .selectMatchByNumber(matchNumber)
            .executeAsOne()

        val field = when (teamNumber) {
            match.r1 -> "r1"
            match.r2 -> "r2"
            match.r3 -> "r3"
            match.b1 -> "b1"
            match.b2 -> "b2"
            match.b3 -> "b3"
            else -> null
        }

        return Pair(match.matchNumber.toInt(), field)
    }

    /*suspend fun fetchGameDetails(): Result<List<Match>> {

    }*/
}