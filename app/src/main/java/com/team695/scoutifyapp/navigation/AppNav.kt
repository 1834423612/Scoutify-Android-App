package com.team695.scoutifyapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.team695.scoutifyapp.ui.InputScreen
import com.team695.scoutifyapp.ui.components.app.structure.MatchSchedule
import com.team695.scoutifyapp.ui.screens.CommentsScreen
import com.team695.scoutifyapp.ui.screens.Form2
import com.team695.scoutifyapp.ui.screens.HomeScreen
import com.team695.scoutifyapp.ui.screens.FormScreen
import com.team695.scoutifyapp.ui.screens.MainScreen
import com.team695.scoutifyapp.ui.screens.PitScoutingScreen
import com.team695.scoutifyapp.ui.screens.tasks.TasksUiState
import com.team695.scoutifyapp.ui.screens.tasks.TasksViewModel


@Composable
fun AppNav(navController: NavHostController, uiState: TasksUiState, tasksViewModel: TasksViewModel) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            MatchSchedule(navController = navController)
        }
        composable(route = "comments") {
            CommentsScreen()
        }
        composable("pitScouting") {
            PitScoutingScreen()
        }
        composable("upload") {
            InputScreen(navController = navController)
        }
        composable(route = "settings") {
            FormScreen()
        }
    }
}
