package com.team695.scoutifyapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.team695.scoutifyapp.data.api.service.LoginService
import com.team695.scoutifyapp.data.api.service.MatchService
import com.team695.scoutifyapp.ui.InputScreen
import com.team695.scoutifyapp.ui.screens.CommentsScreen
import com.team695.scoutifyapp.ui.screens.home.HomeScreen
import com.team695.scoutifyapp.ui.screens.FormScreen
import com.team695.scoutifyapp.ui.screens.PitScoutingScreen
import com.team695.scoutifyapp.ui.viewModels.HomeViewModel
import com.team695.scoutifyapp.ui.viewModels.ViewModelFactory
import com.team695.scoutifyapp.data.api.service.TaskService
import com.team695.scoutifyapp.ui.screens.dataCollection.DataScreen
import com.team695.scoutifyapp.ui.screens.login.LoginScreen
import com.team695.scoutifyapp.ui.viewModels.DataViewModel
import com.team695.scoutifyapp.ui.viewModels.LoginViewModel


@Composable
fun AppNav(
    navController: NavHostController,
    taskService: TaskService,
    matchService: MatchService,
    loginService: LoginService
) {
    val owner: ViewModelStoreOwner = LocalViewModelStoreOwner.current
        ?: throw IllegalStateException("Root must be attached to a ViewModelStoreOwner")

    NavHost(navController = navController, startDestination = "login") {
        composable("home") {

            val homeViewModel: HomeViewModel = viewModel(
                viewModelStoreOwner = owner,
                factory = ViewModelFactory { HomeViewModel(matchService, taskService) }
            )

            HomeScreen(navController = navController, homeViewModel = homeViewModel)

        }
        composable(route = "data") {

            val dataViewModel: DataViewModel = viewModel(
                viewModelStoreOwner = owner,
                factory = ViewModelFactory { DataViewModel() }
            )

            DataScreen(navController = navController, dataViewModel = dataViewModel)
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

        composable(route = "login") { navBackStackEntry ->

            val loginViewModel: LoginViewModel = viewModel(
                viewModelStoreOwner = navBackStackEntry,
                factory = ViewModelFactory { LoginViewModel(loginService) }
            )

            LoginScreen(navController = navController, loginViewModel = loginViewModel)
        }
    }
}
