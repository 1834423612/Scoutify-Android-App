package com.team695.scoutifyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.team695.scoutifyapp.data.api.CasdoorClient
import com.team695.scoutifyapp.data.api.ScoutifyClient
import com.team695.scoutifyapp.data.api.service.LoginService
import com.team695.scoutifyapp.data.api.service.MatchService
import com.team695.scoutifyapp.ui.theme.ScoutifyTheme
import com.team695.scoutifyapp.data.api.service.TaskService
import com.team695.scoutifyapp.data.repository.MatchRepository
import com.team695.scoutifyapp.data.repository.TaskRepository
import com.team695.scoutifyapp.data.repository.UserRepository
import com.team695.scoutifyapp.db.AppDatabase
import com.team695.scoutifyapp.db.GameDetailsEntity
import app.cash.sqldelight.ColumnAdapter
import com.team695.scoutifyapp.data.api.service.GameDetailsService
import com.team695.scoutifyapp.data.repository.GameDetailRepository

val booleanAdapter = object : ColumnAdapter<Boolean, Long> {
    override fun decode(databaseValue: Long): Boolean = databaseValue == 1L
    override fun encode(value: Boolean): Long = if (value) 1L else 0L
}

val intAdapter = object : ColumnAdapter<Int, Long> {
    override fun decode(databaseValue: Long): Int = databaseValue.toInt()
    override fun encode(value: Int): Long = value.toLong()
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            )
        )
        db.taskQueries.seedData() //to add default data
        db.matchQueries.seedData() //to add default data

        val taskService = TaskService(db = db)
        val matchService: MatchService = ScoutifyClient.matchService
        val loginService: LoginService = CasdoorClient.loginService
        val gameDetailsService: GameDetailsService = GameDetailsService()

        val userRepository = UserRepository(service = loginService, db = db)
        val taskRepository = TaskRepository(service = taskService, db = db)
        val matchRepository = MatchRepository(service = matchService, db = db)
        val gameDetailRepository = GameDetailRepository(service = gameDetailsService, db = db)


        setContent {
            ScoutifyTheme {
                Root(
                    taskRepository = taskRepository,
                    matchRepository = matchRepository,
                    userRepository = userRepository,
                    gameDetailRepository = gameDetailRepository,
                    database = db
                )
            }
        }


    }
}
