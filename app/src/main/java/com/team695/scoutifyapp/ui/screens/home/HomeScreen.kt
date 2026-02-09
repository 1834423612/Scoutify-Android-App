package com.team695.scoutifyapp.ui.screens.home

import com.team695.scoutifyapp.ui.screens.home.MatchSchedule
import com.team695.scoutifyapp.ui.screens.home.TasksCard
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.team695.scoutifyapp.ui.viewModels.TasksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: TasksViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.weight(0.33f)) {
            TasksCard(onTabSelected = {x: Int -> x} ) //placeholder for onTabSelected
        }
        Box(modifier = Modifier.weight(0.67f)) {
            MatchSchedule(onCommentClicked = {navController.navigate("comments")})
        }
    }
}