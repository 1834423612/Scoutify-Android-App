package com.team695.scoutifyapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.team695.scoutifyapp.ui.InputScreen
import com.team695.scoutifyapp.ui.components.app.structure.MatchSchedule
import com.team695.scoutifyapp.ui.screens.Form2
import com.team695.scoutifyapp.ui.screens.HomeScreen
import com.team695.scoutifyapp.ui.screens.FormScreen
import com.team695.scoutifyapp.ui.screens.MainScreen
import com.team695.scoutifyapp.ui.screens.PitScoutingScreen



@Composable
fun AppNav(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            MatchSchedule()
        }
        composable("pitScouting") {
            PitScoutingScreen()
        }
        composable("upload") {
            InputScreen(navController = navController)
        }
        composable(route = "settings") {
            FormScreen()
        }
    }
}
