package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.team695.scoutifyapp.ui.components.InfoCard
import com.team695.scoutifyapp.ui.components.MatchBox



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchSchedule(navigateToMatchForm: () -> Unit, back:()->Unit) {
    Scaffold{ padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            InfoCard(
                title = "Match Schedule",
                description = "Welcome to our scouting app!"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = navigateToMatchForm) {
                Text("test")
            }
            Button(onClick = back) {
                Text("Home")
            }

            MatchBox(
                matchNumber = "Match 21",
                timestamp = "Oct 18, 12:00",
                redTeams = listOf(48, 9999, 2399),
                blueTeams = listOf(9997, 3193, 4611),
            )
            MatchBox(
                matchNumber = "Match 22",
                timestamp = "Oct 18, 12:00",
                redTeams = listOf(48, 9999, 2399),
                blueTeams = listOf(9997, 3193, 4611),
            )
        }
    }
}
