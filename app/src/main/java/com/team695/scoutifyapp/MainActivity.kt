package com.team695.scoutifyapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.team695.scoutifyapp.data.api.NetworkMonitor
import com.team695.scoutifyapp.data.api.client.CasdoorClient
import com.team695.scoutifyapp.data.api.client.ScoutifyClient
import com.team695.scoutifyapp.data.api.model.GameConstantsStore
import com.team695.scoutifyapp.data.api.model.User
import com.team695.scoutifyapp.data.api.service.CommentService
import com.team695.scoutifyapp.data.api.service.LoginService
import com.team695.scoutifyapp.data.api.service.MatchService
import com.team695.scoutifyapp.ui.theme.ScoutifyTheme
import com.team695.scoutifyapp.data.api.service.TaskService
import com.team695.scoutifyapp.data.repository.MatchRepository
import com.team695.scoutifyapp.data.repository.TaskRepository
import com.team695.scoutifyapp.data.repository.UserRepository
import com.team695.scoutifyapp.db.AppDatabase
import com.team695.scoutifyapp.db.GameDetailsEntity
import com.team695.scoutifyapp.db.CommentsEntity
import com.team695.scoutifyapp.data.api.service.GameDetailsService
import com.team695.scoutifyapp.data.api.service.UserService
import com.team695.scoutifyapp.data.charAdapter
import com.team695.scoutifyapp.data.intAdapter
import com.team695.scoutifyapp.data.repository.CommentRepository
import com.team695.scoutifyapp.data.repository.GameDetailRepository
import com.team695.scoutifyapp.db.GameConstantsEntity
import com.team695.scoutifyapp.ui.extensions.androidID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MAIN", "Android_ID: ${applicationContext.androidID}")

        ScoutifyClient.initialize(applicationContext)

        val driver = AndroidSqliteDriver(
            schema = AppDatabase.Schema,
            context = applicationContext,
            name = "scoutify_test.db" // Using a test name to avoid messing up real data
        )

        val db = AppDatabase(
            driver = driver,
            gameDetailsEntityAdapter = GameDetailsEntity.Adapter(
                idAdapter = intAdapter,
                task_idAdapter = intAdapter,
                auton_fuel_countAdapter = intAdapter,
                transition_cycling_timeAdapter = intAdapter,
                transition_stockpiling_timeAdapter = intAdapter,
                transition_defending_timeAdapter = intAdapter,
                transition_broken_timeAdapter = intAdapter,
                shift1_cycling_timeAdapter = intAdapter,
                shift1_stockpiling_timeAdapter = intAdapter,
                shift1_defending_timeAdapter = intAdapter,
                shift1_broken_timeAdapter = intAdapter,
                shift2_cycling_timeAdapter = intAdapter,
                shift2_stockpiling_timeAdapter = intAdapter,
                shift2_defending_timeAdapter = intAdapter,
                shift2_broken_timeAdapter = intAdapter,
                shift3_cycling_timeAdapter = intAdapter,
                shift3_stockpiling_timeAdapter = intAdapter,
                shift3_defending_timeAdapter = intAdapter,
                shift3_broken_timeAdapter = intAdapter,
                shift4_cycling_timeAdapter = intAdapter,
                shift4_stockpiling_timeAdapter = intAdapter,
                shift4_defending_timeAdapter = intAdapter,
                shift4_broken_timeAdapter = intAdapter,
                endgame_cycling_timeAdapter = intAdapter,
                endgame_stockpiling_timeAdapter = intAdapter,
                endgame_defending_timeAdapter = intAdapter,
                endgame_broken_timeAdapter = intAdapter,
                teleop_fuel_countAdapter = intAdapter,
            ),
            commentsEntityAdapter = CommentsEntity.Adapter(
                match_numberAdapter = intAdapter,
                team_numberAdapter  = intAdapter,
                alliance_positionAdapter = intAdapter,
                submittedAdapter = intAdapter
            ),
            gameConstantsEntityAdapter = GameConstantsEntity.Adapter(
                frc_season_master_sm_yearAdapter = intAdapter,
                game_matchup_gm_game_typeAdapter = charAdapter
            )
        )

        db.gameConstantsQueries.seedData()

        GameConstantsStore.update(
            db.gameConstantsQueries.getConstants().executeAsOne()
        )

        val taskService = ScoutifyClient.taskService
        val matchService: MatchService = ScoutifyClient.matchService
        val loginService: LoginService = CasdoorClient.loginService
        val userService: UserService = ScoutifyClient.userService
        val commentService: CommentService = ScoutifyClient.commentService
        val gameDetailsService: GameDetailsService = ScoutifyClient.gameDetailsService

        val userRepository = UserRepository(
            loginService = loginService,
            userService = userService,
            db = db,
            context = applicationContext
        )
        val taskRepository = TaskRepository(service = taskService, db = db)
        val matchRepository = MatchRepository(service = matchService, db = db)
        val gameDetailRepository = GameDetailRepository(service = gameDetailsService, db = db)
        val commentRepository = CommentRepository(db = db, service = commentService)

        val networkMonitor = NetworkMonitor(applicationContext)

        setContent {
            ScoutifyTheme {
                Root(
                    taskRepository = taskRepository,
                    matchRepository = matchRepository,
                    userRepository = userRepository,
                    gameDetailRepository = gameDetailRepository,
                    commentRepository = commentRepository,
                    networkMonitor = networkMonitor,
                )
            }
        }

    }
}
