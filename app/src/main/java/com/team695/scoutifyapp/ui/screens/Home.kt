package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.team695.scoutifyapp.ui.components.InfoCard
import com.team695.scoutifyapp.ui.components.Bison


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navigateToPitSchedule: () -> Unit, navigateToMatchSchedule:()->Unit) {
    Scaffold{ padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            InfoCard(
                title = "Team 695",
                description = "Welcome to our scouting app!"
            )


            Bison()


            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = navigateToPitSchedule) {
                Text("Pit Scouting Assignments")
            }


            Button(onClick = navigateToMatchSchedule) {
                Text("Match Scouting Assignments ")
            }



        }
    }
}
