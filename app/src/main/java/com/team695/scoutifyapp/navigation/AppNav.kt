package com.team695.scoutifyapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.team695.scoutifyapp.data.api.service.MatchService
import com.team695.scoutifyapp.ui.InputScreen
import com.team695.scoutifyapp.ui.screens.CommentsScreen
import com.team695.scoutifyapp.ui.screens.home.HomeScreen
import com.team695.scoutifyapp.ui.screens.FormScreen
import com.team695.scoutifyapp.ui.screens.PitScoutingScreen
import com.team695.scoutifyapp.ui.viewModels.HomeViewModel
import com.team695.scoutifyapp.ui.viewModels.ViewModelFactory
import com.team695.scoutifyapp.data.api.service.TaskService


@Composable
fun AppNav(
    navController: NavHostController,
    taskService: TaskService,
    matchService: MatchService,
) {
    val owner: ViewModelStoreOwner = LocalViewModelStoreOwner.current
        ?: throw IllegalStateException("Root must be attached to a ViewModelStoreOwner")

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {

            val homeViewModel: HomeViewModel = viewModel(
                viewModelStoreOwner = owner,
                factory = ViewModelFactory { HomeViewModel(matchService, taskService) }
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
