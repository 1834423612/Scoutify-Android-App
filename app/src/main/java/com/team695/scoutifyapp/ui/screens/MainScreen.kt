package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team695.scoutifyapp.ui.theme.ScoutifyTheme

@Composable
fun MainScreen(navController: androidx.navigation.NavHostController) {
    ScoutifyTheme {
        val sampleMatches = listOf(
            MatchItem("Q1", "Team 695", 132, "Red"),
            MatchItem("Q2", "Team 291", 118, "Blue"),
            MatchItem("Q3", "Team 379", 141, "Red"),
            MatchItem("Q4", "Team 217", 97, "Blue")
        )

        Home(
            matches = sampleMatches,
            onAddMatch = { navController.navigate("MatchForm") },
            onSettingsClick = { /* TODO: Setting Page */ },
            onMatchClick = { matchNumber ->
                navController.navigate("MatchDetail/$matchNumber")
            },
            onPitScoutingClick = {
                navController.navigate("PitForm")
            }
        )
    }
}
