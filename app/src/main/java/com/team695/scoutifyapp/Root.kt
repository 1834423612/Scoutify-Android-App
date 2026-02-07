package com.team695.scoutifyapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.team695.scoutifyapp.navigation.AppNav
import com.team695.scoutifyapp.ui.components.app.structure.NavRail
import com.team695.scoutifyapp.ui.screens.ViewModelFactory
import com.team695.scoutifyapp.ui.screens.tasks.TaskService
import com.team695.scoutifyapp.ui.screens.tasks.TasksViewModel
import com.team695.scoutifyapp.ui.theme.*

@Composable
fun Root() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        val taskService = TaskService()
        val factory = ViewModelFactory { TasksViewModel(taskService) }
        val tasksViewModel: TasksViewModel = viewModel(factory = factory)
        val uiState by tasksViewModel.uiState.collectAsState()
        val navController: NavHostController = rememberNavController()

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            NavRail(
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToPitScouting = { navController.navigate(route="pitScouting") },
                onNavigateToUpload = { navController.navigate(route="upload") },
                onNavigateToSettings = { navController.navigate(route="settings") },
                navController = navController
            )

            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(0.67f)) {
                    AppNav(navController = navController)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
fun RootPreview() {
    ScoutifyTheme {
        Root()
    }
}
