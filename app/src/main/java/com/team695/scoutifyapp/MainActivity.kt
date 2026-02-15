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
import com.team695.scoutifyapp.db.AppDatabase
import com.team695.scoutifyapp.db.AppDatabase.Companion.invoke
import com.team695.scoutifyapp.ui.screens.login.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ScoutifyClient.initialize(applicationContext)

        val driver = AndroidSqliteDriver(
            schema = AppDatabase.Schema,
            context = applicationContext,
            name = "scoutify_test.db" // Using a test name to avoid messing up real data
        )

        val db = AppDatabase(driver)

        val taskService = TaskService()
        val matchService: MatchService = ScoutifyClient.matchService
        val loginService: LoginService = CasdoorClient.loginService

        taskService.initialize(db)

        setContent {
            ScoutifyTheme {
                Root(
                    taskService = taskService,
                    matchService = matchService,
                    loginService = loginService
                )
            }
        }
    }
}
