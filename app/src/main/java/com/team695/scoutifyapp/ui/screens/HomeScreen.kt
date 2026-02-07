package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.team695.scoutifyapp.ui.components.app.structure.MatchSchedule
import com.team695.scoutifyapp.ui.components.app.structure.TasksCard
import com.team695.scoutifyapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController
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