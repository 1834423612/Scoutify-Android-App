package com.team695.scoutifyapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.team695.scoutifyapp.ui.InputScreen
import com.team695.scoutifyapp.ui.screens.CommentsScreen
import com.team695.scoutifyapp.ui.screens.home.HomeScreen
import com.team695.scoutifyapp.ui.screens.FormScreen
import com.team695.scoutifyapp.ui.screens.PitScoutingScreen
import com.team695.scoutifyapp.ui.viewModels.ViewModelFactory
import com.team695.scoutifyapp.ui.viewModels.TaskService
import com.team695.scoutifyapp.ui.viewModels.TasksViewModel


@Composable
fun AppNav(
    navController: NavHostController,
    taskService: TaskService
) {
    val owner: ViewModelStoreOwner = LocalViewModelStoreOwner.current
        ?: throw IllegalStateException("Root must be attached to a ViewModelStoreOwner")

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {

            val homeViewModel: TasksViewModel = viewModel(
                viewModelStoreOwner = owner,
                factory = ViewModelFactory<TasksViewModel> { TasksViewModel(taskService) }
            )

            HomeScreen(navController = navController, homeViewModel = homeViewModel)

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
