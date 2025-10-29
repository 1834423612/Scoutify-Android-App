package com.team695.scoutifyapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController

@Composable
fun TopBarMenu(navController: NavHostController) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.Menu, contentDescription = "Menu")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Home") },
                onClick = {
                    navController.navigate("Home")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Pit Scouting") },
                onClick = {
                    navController.navigate("PitSchedule")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Match Scouting") },
                onClick = {
                    navController.navigate("MatchSchedule")
                    expanded = false
                }
            )
        }
    }
}
