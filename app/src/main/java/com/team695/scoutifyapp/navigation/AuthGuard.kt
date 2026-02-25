package com.team695.scoutifyapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.team695.scoutifyapp.data.api.model.User
import com.team695.scoutifyapp.data.repository.GameDetailRepository
import com.team695.scoutifyapp.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AuthGuard(
    userRepository: UserRepository,
    navController: NavController,
    gameDetailRepository: GameDetailRepository,
    content: @Composable () -> Unit
) {

    val user by userRepository.currentUser.collectAsState(
        initial = User(
            name = "LOADING"
        )
    )

    if (user?.name == "WRONG_USER" || user == null) {
        return navController.navigate("login") {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
    }

    if (user != null && user?.name != "LOADING") {

        LaunchedEffect(Unit) {
            gameDetailRepository.setGameConstants()
        }
    }

    content()
}