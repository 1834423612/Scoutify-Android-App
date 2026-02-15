package com.team695.scoutifyapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.team695.scoutifyapp.data.api.CasdoorClient
import com.team695.scoutifyapp.data.api.ScoutifyClient
import com.team695.scoutifyapp.data.api.service.LoginService
import com.team695.scoutifyapp.data.api.service.MatchService
import com.team695.scoutifyapp.navigation.AppNav
import com.team695.scoutifyapp.ui.components.NavRail
import com.team695.scoutifyapp.data.api.service.TaskService
//import com.team695.scoutifyapp.ui.theme.*
import com.team695.scoutifyapp.db.AppDatabase
import com.team695.scoutifyapp.ui.theme.Background
import com.team695.scoutifyapp.ui.theme.ScoutifyTheme
import kotlin.math.log

@Composable
fun Root(
    taskService: TaskService,
    matchService: MatchService,
    loginService: LoginService
) {
    val context = LocalContext.current
// Create SQLDelight DB here (NOT inside LaunchedEffect)

    LaunchedEffect(Unit) {
        // 1. Setup DB Driver (Just for this test)

        val queries = taskService.db.taskQueries
        val queries2= taskService.db.pitscoutQueries
        println("--- SQL TEST PitScout START ---")

        println(queries2)
        println("Inserting 'Test pitsout'...")
        queries2.insertPitscout("ohcl","0001","{name:'test'}","","Clarence","","","")
//        val pitscouts=queries2.selectAllPitscout().executeAsList()
//
//        println("Found ${queries2.size} tasks:")
//        queries2.forEach { task ->
//            println(" -> ID: ${task.id} | Title: ${task.title} | Completed: ${task.isCompleted}")
//        }
        val pitscouts = queries2.selectAllPitscout().executeAsList()

        println("Found ${pitscouts.size} pitscout rows:")
        pitscouts.forEach { row ->
            println(" -> ID: ${row.id} | Event: ${row.event_id} | Form: ${row.form_id}")
        }



        // 2. CLEAR previous test data (Optional, so you don't see duplicates every run)
        // You might need to add a "deleteAll: DELETE FROM taskEntity;" query to Task.sq first
        // queries.deleteAll()

        println("--- SQL TEST START ---")

        // 3. INSERT a test task
        println("Inserting 'Test Task'...")
        queries.insertTask(title = "Test Task via Kotlin", isCompleted = 0)

        // 4. READ and PRINT all tasks
        val tasks = queries.selectAllTasks().executeAsList() // .executeAsList() grabs them instantly

        println("Found ${tasks.size} tasks:")
        tasks.forEach { task ->
            println(" -> ID: ${task.id} | Title: ${task.title} | Completed: ${task.isCompleted}")
        }

        println("--- SQL TEST END ---")
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        val navController: NavHostController = rememberNavController()

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            NavRail(
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToPitScouting = { navController.navigate(route="pitScouting") },
                onNavigateToUpload = { navController.navigate(route="upload") },
                onNavigateToSettings = { navController.navigate(route="settings") },
                onNavigateToLogin = { navController.navigate(route="login") },
                navController = navController
            )

            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(0.67f)) {
                    AppNav(
                        navController = navController,
                        taskService = taskService,
                        matchService = matchService,
                        loginService = loginService,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
fun RootPreview() {
    val taskService = TaskService()
    val matchService = ScoutifyClient.matchService
    val loginService = CasdoorClient.loginService

    ScoutifyTheme {
        Root(
            taskService = taskService,
            matchService = matchService,
            loginService = loginService,
        )
    }
}
