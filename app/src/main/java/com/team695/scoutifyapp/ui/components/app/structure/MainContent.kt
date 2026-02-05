package com.team695.scoutifyapp.ui.components.app.structure

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.team695.scoutifyapp.navigation.AppNav
import com.team695.scoutifyapp.ui.screens.ViewModelFactory
import com.team695.scoutifyapp.ui.screens.tasks.TaskRepository
import com.team695.scoutifyapp.ui.screens.tasks.TasksViewModel

@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    tasksViewModel: TasksViewModel = viewModel(
        factory = ViewModelFactory {
            TasksViewModel(repository = TaskRepository())
        }
    ),
    navController: NavHostController
) {
    val uiState by tasksViewModel.uiState.collectAsState()

    Row(
        modifier = modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.weight(0.67f)) {
            AppNav(navController = navController, uiState = uiState, tasksViewModel = tasksViewModel)
        }
    }
}
