package com.team695.scoutifyapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.team695.scoutifyapp.data.api.NetworkMonitor
import com.team695.scoutifyapp.ui.InputScreen
import com.team695.scoutifyapp.ui.screens.CommentsScreen
import com.team695.scoutifyapp.ui.screens.home.HomeScreen
import com.team695.scoutifyapp.ui.screens.FormScreen
import com.team695.scoutifyapp.ui.screens.PitScoutingScreen
import com.team695.scoutifyapp.ui.viewModels.HomeViewModel
import com.team695.scoutifyapp.ui.viewModels.PitScoutingViewModel
import com.team695.scoutifyapp.ui.viewModels.ViewModelFactory
import com.team695.scoutifyapp.data.repository.GameDetailRepository
import com.team695.scoutifyapp.data.repository.MatchRepository
import com.team695.scoutifyapp.data.repository.TaskRepository
import com.team695.scoutifyapp.data.repository.UserRepository
import com.team695.scoutifyapp.ui.screens.data.DataScreen
import com.team695.scoutifyapp.ui.screens.login.LoginScreen
import com.team695.scoutifyapp.ui.viewModels.DataViewModel
import com.team695.scoutifyapp.ui.viewModels.LoginViewModel


@Composable
fun AppNav(
    navController: NavHostController,
    taskRepository: TaskRepository,
    matchRepository: MatchRepository,
    userRepository: UserRepository,
    gameDetailRepository: GameDetailRepository,
    networkMonitor: NetworkMonitor,
) {
    val owner: ViewModelStoreOwner = LocalViewModelStoreOwner.current
        ?: throw IllegalStateException("Root must be attached to a ViewModelStoreOwner")

    NavHost(navController = navController, startDestination = "login") {
        composable("home") {
            AuthGuard(
                userRepository = userRepository,
                gameDetailRepository = gameDetailRepository,
                navController = navController,
            ) {
                val homeViewModel: HomeViewModel = viewModel(
                    viewModelStoreOwner = owner,
                    factory = ViewModelFactory { HomeViewModel(
                        taskRepository = taskRepository,
                        matchRepository = matchRepository,
                        gameDetailRepository = gameDetailRepository,
                        networkMonitor = networkMonitor,
                    ) }
                )

                HomeScreen(navController = navController, homeViewModel = homeViewModel)
            }
        }
        composable(
            route = "data/{taskId}",
            arguments = listOf(
                    navArgument("taskId") { type = NavType.IntType}
            )
        ) { navBackStackEntry ->
            AuthGuard(
                userRepository = userRepository,
                navController = navController,
                gameDetailRepository = gameDetailRepository
            ) {
                val dataScreenOwner: ViewModelStoreOwner = LocalViewModelStoreOwner.current
                    ?: throw IllegalStateException("Root must be attached to a ViewModelStoreOwner")

                val taskId = navBackStackEntry.arguments?.getInt("taskId")
                    ?: return@AuthGuard navController.navigate("home")

                val dataViewModel: DataViewModel = viewModel(
                    viewModelStoreOwner = dataScreenOwner,
                    factory = ViewModelFactory {
                        DataViewModel(
                            gameDetailRepository = gameDetailRepository,
                            taskRepository = taskRepository,
                            taskId = taskId
                        )
                    }
                )

                DataScreen(navController = navController, dataViewModel = dataViewModel)
            }
        }
        composable(route = "comments") {
            AuthGuard(
                userRepository = userRepository,
                navController = navController,
                gameDetailRepository = gameDetailRepository
            ) {
                CommentsScreen()
            }
        }
        composable("pitScouting") {
            AuthGuard(
                userRepository = userRepository,
                navController = navController,
                gameDetailRepository = gameDetailRepository
            ) {
                val pitScoutingViewModel: PitScoutingViewModel = viewModel(
                    factory = ViewModelFactory { PitScoutingViewModel() }
                )
                PitScoutingScreen(viewModel = pitScoutingViewModel)
            }
        }
        composable("upload") {
            AuthGuard(
                userRepository = userRepository,
                navController = navController,
                gameDetailRepository = gameDetailRepository
            ) {
                InputScreen(navController = navController)
            }
        }
        composable(route = "settings") {
            AuthGuard(
                userRepository = userRepository,
                navController = navController,
                gameDetailRepository = gameDetailRepository
            ) {
                FormScreen()
            }
        }

        composable(route = "login") { navBackStackEntry ->
            val loginViewModel: LoginViewModel = viewModel(
                viewModelStoreOwner = navBackStackEntry,
                factory = ViewModelFactory { LoginViewModel(userRepository) }
            )

            LoginScreen(navController = navController, loginViewModel = loginViewModel)
        }
    }
}
