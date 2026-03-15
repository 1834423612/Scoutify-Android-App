package com.team695.scoutifyapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.team695.scoutifyapp.data.api.NetworkMonitor
import com.team695.scoutifyapp.data.repository.CommentRepository
import com.team695.scoutifyapp.data.repository.GameDetailRepository
import com.team695.scoutifyapp.data.repository.MatchRepository
import com.team695.scoutifyapp.data.repository.PitScoutingRepository
import com.team695.scoutifyapp.data.repository.TaskRepository
import com.team695.scoutifyapp.data.repository.TeamNameRepository
import com.team695.scoutifyapp.data.repository.UserRepository
import com.team695.scoutifyapp.navigation.AppNav
import com.team695.scoutifyapp.ui.components.NavRail
import com.team695.scoutifyapp.ui.theme.Background

@Composable
fun Root(
    taskRepository: TaskRepository,
    matchRepository: MatchRepository,
    userRepository: UserRepository,
    gameDetailRepository: GameDetailRepository,
    commentRepository: CommentRepository,
    teamNameRepository: TeamNameRepository,
    pitScoutingRepository: PitScoutingRepository,
    networkMonitor: NetworkMonitor
) {
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
                onNavigateToPitScouting = { navController.navigate(route = "pitScouting") },
                onNavigateToUpload = { navController.navigate(route = "upload") },
                onNavigateToSettings = { navController.navigate(route = "settings") },
                onNavigateToLogin = { navController.navigate(route = "login") },
                userRepository = userRepository,
                navController = navController
            )

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(0.67f)) {
                    AppNav(
                        navController = navController,
                        taskRepository = taskRepository,
                        matchRepository = matchRepository,
                        userRepository = userRepository,
                        gameDetailRepository = gameDetailRepository,
                        commentRepository = commentRepository,
                        teamNameRepository = teamNameRepository,
                        pitScoutingRepository = pitScoutingRepository,
                        networkMonitor = networkMonitor
                    )
                }
            }
        }
    }
}
