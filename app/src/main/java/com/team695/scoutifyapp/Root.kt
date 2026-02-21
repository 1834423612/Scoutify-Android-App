package com.team695.scoutifyapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.team695.scoutifyapp.navigation.AppNav
import com.team695.scoutifyapp.ui.components.NavRail
import com.team695.scoutifyapp.data.repository.GameDetailRepository
import com.team695.scoutifyapp.data.repository.MatchRepository
import com.team695.scoutifyapp.data.repository.TaskRepository
import com.team695.scoutifyapp.data.repository.UserRepository
//import com.team695.scoutifyapp.ui.theme.*
import com.team695.scoutifyapp.ui.theme.Background

@Composable
fun Root(
    taskRepository: TaskRepository,
    matchRepository: MatchRepository,
    userRepository: UserRepository,
    gameDetailRepository: GameDetailRepository,
) {
    val context = LocalContext.current

    /*
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
     */

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        val navController: NavHostController = rememberNavController()

        Row(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            NavRail(
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToPitScouting = { navController.navigate(route="pitScouting") },
                onNavigateToUpload = { navController.navigate(route="upload") },
                onNavigateToSettings = { navController.navigate(route="settings") },
                onNavigateToLogin = { navController.navigate(route="login") },
                userRepository = userRepository,
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
                        taskRepository = taskRepository,
                        matchRepository = matchRepository,
                        userRepository = userRepository,
                        gameDetailRepository = gameDetailRepository,
                    )
                }
            }
        }
    }
}

/*
TODO: How to add db?

@Preview(showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
fun RootPreview() {
    val taskService = TaskService()
    val matchService = ScoutifyClient.matchService
    val user = CasdoorClient.loginService

    ScoutifyTheme {
        Root(
            taskService = taskService,
            matchService = matchService,
            loginService = loginService,
        )
    }
}
 */
