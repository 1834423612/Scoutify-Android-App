package com.team695.scoutifyapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.team695.scoutifyapp.config.DebugConfig
import com.team695.scoutifyapp.data.api.NetworkMonitor
import com.team695.scoutifyapp.data.repository.CommentRepository
import com.team695.scoutifyapp.data.repository.GameDetailRepository
import com.team695.scoutifyapp.data.repository.LocalDatabaseDebugRepository
import com.team695.scoutifyapp.data.repository.MatchRepository
import com.team695.scoutifyapp.data.repository.PitScoutingRepository
import com.team695.scoutifyapp.data.repository.TaskRepository
import com.team695.scoutifyapp.data.repository.TeamNameRepository
import com.team695.scoutifyapp.data.repository.UserRepository
import com.team695.scoutifyapp.ui.InputScreen
import com.team695.scoutifyapp.ui.screens.CommentsScreen
import com.team695.scoutifyapp.ui.screens.CommentsViewModel
import com.team695.scoutifyapp.ui.screens.FormScreen
import com.team695.scoutifyapp.ui.screens.PitScoutingScreen
import com.team695.scoutifyapp.ui.screens.data.DataScreen
import com.team695.scoutifyapp.ui.screens.home.HomeScreen
import com.team695.scoutifyapp.ui.screens.login.LoginScreen
import com.team695.scoutifyapp.ui.viewModels.DataViewModel
import com.team695.scoutifyapp.ui.viewModels.HomeViewModel
import com.team695.scoutifyapp.ui.viewModels.LoginViewModel
import com.team695.scoutifyapp.ui.viewModels.PitScoutingViewModel
import com.team695.scoutifyapp.ui.viewModels.SettingsViewModel
import com.team695.scoutifyapp.ui.viewModels.ViewModelFactory
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text

@Composable
fun AppNav(
    navController: NavHostController,
    taskRepository: TaskRepository,
    matchRepository: MatchRepository,
    userRepository: UserRepository,
    commentRepository: CommentRepository,
    gameDetailRepository: GameDetailRepository,
    teamNameRepository: TeamNameRepository,
    localDatabaseDebugRepository: LocalDatabaseDebugRepository,
    pitScoutingRepository: PitScoutingRepository,
    networkMonitor: NetworkMonitor
) {
    val owner: ViewModelStoreOwner = LocalViewModelStoreOwner.current
        ?: throw IllegalStateException("Root must be attached to a ViewModelStoreOwner")

    val startDestination = if (DebugConfig.BYPASS_AUTH) "home" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("home") {
            AuthGuard(
                userRepository = userRepository,
                gameDetailRepository = gameDetailRepository,
                navController = navController
            ) {
                val homeViewModel: HomeViewModel = viewModel(
                    viewModelStoreOwner = owner,
                    factory = ViewModelFactory {
                        HomeViewModel(
                            taskRepository = taskRepository,
                            matchRepository = matchRepository,
                            gameDetailRepository = gameDetailRepository,
                            networkMonitor = networkMonitor
                        )
                    }
                )

                HomeScreen(navController = navController, homeViewModel = homeViewModel)
            }
        }

        composable(
            route = "data/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { navBackStackEntry ->
            AuthGuard(
                userRepository = userRepository,
                navController = navController,
                gameDetailRepository = gameDetailRepository
            ) {
                val dataScreenOwner: ViewModelStoreOwner = LocalViewModelStoreOwner.current
                    ?: throw IllegalStateException(
                        "Root must be attached to a ViewModelStoreOwner"
                    )
                val taskId = navBackStackEntry.arguments?.getInt("taskId")
                    ?: return@AuthGuard navController.navigate("home")

                val dataViewModel: DataViewModel = viewModel(
                    viewModelStoreOwner = dataScreenOwner,
                    factory = ViewModelFactory {
                        DataViewModel(
                            gameDetailRepository = gameDetailRepository,
                            taskRepository = taskRepository,
                            matchRepository = matchRepository,
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
                val matchNumber = navController
                    .previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<Int>("matchNumber")

                val commentsViewModel: CommentsViewModel = viewModel(
                    viewModelStoreOwner = owner,
                    factory = ViewModelFactory {
                        CommentsViewModel(
                            commentRepository = commentRepository,
                            matchRepository = matchRepository,
                            teamNameRepository = teamNameRepository
                        )
                    }
                )

                LaunchedEffect(matchNumber) {
                    if (matchNumber != -1) {
                        commentsViewModel.onMatchSelected(matchNumber.toString())
                    }
                }

                CommentsScreen(viewModel = commentsViewModel, matchNumber = matchNumber)
            }
        }

        composable("pitScouting") {
            AuthGuard(
                userRepository = userRepository,
                navController = navController,
                gameDetailRepository = gameDetailRepository
            ) {
                val pitScoutingViewModel: PitScoutingViewModel = viewModel(
                    viewModelStoreOwner = owner,
                    factory = ViewModelFactory {
                        PitScoutingViewModel(repository = pitScoutingRepository)
                    }
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
                if (DebugConfig.ENABLE_LOCAL_DATABASE_DEBUGGING) {
                    val settingsViewModel: SettingsViewModel = viewModel(
                        viewModelStoreOwner = owner,
                        factory = ViewModelFactory {
                            SettingsViewModel(
                                localDatabaseDebugRepository = localDatabaseDebugRepository
                            )
                        }
                    )

                    FormScreen(settingsViewModel = settingsViewModel)
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Settings debug tools are unavailable in release builds.")
                    }
                }
            }
        }

        composable(route = "login") { navBackStackEntry ->
            val loginViewModel: LoginViewModel = viewModel(
                viewModelStoreOwner = navBackStackEntry,
                factory = ViewModelFactory { LoginViewModel(userRepository) }
            )

            LoginScreen(
                navController = navController,
                loginViewModel = loginViewModel,
                networkMonitor = networkMonitor,
            )
        }
    }
}

