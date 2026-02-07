package com.team695.scoutifyapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.team695.scoutifyapp.ui.InputScreen
import com.team695.scoutifyapp.ui.screens.CommentsScreen
import com.team695.scoutifyapp.ui.screens.home.HomeScreen
import com.team695.scoutifyapp.ui.screens.FormScreen
import com.team695.scoutifyapp.ui.screens.PitScoutingScreen
import com.team695.scoutifyapp.ui.screens.ViewModelFactory
import com.team695.scoutifyapp.ui.viewModels.TaskService
import com.team695.scoutifyapp.ui.viewModels.TasksViewModel


@Composable
fun AppNav(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {

            val viewModel: TasksViewModel = remember(it) {
                val taskService: TaskService = TaskService()
                val factory: ViewModelFactory<TasksViewModel> = ViewModelFactory{ TasksViewModel(taskService) }

                ViewModelProvider(it, factory).get(TasksViewModel::class.java)
            }

            HomeScreen(navController = navController, viewModel = viewModel)
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
