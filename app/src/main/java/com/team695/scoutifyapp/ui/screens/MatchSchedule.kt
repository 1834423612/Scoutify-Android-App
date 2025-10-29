package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.team695.scoutifyapp.ui.components.InfoCard
import com.team695.scoutifyapp.ui.components.TopBarMenu

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
        }
    }
}
