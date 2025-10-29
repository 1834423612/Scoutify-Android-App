package com.team695.scoutifyapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.team695.scoutifyapp.ui.screens.*


@Composable
fun AppNav(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "Home") {
        composable("Home") {
            Home(
                navigateToPitSchedule = { navController.navigate("PitSchedule") },
                navigateToMatchSchedule = {navController.navigate("MatchSchedule")}
            )
        }
        composable("MatchSchedule") {
            MatchSchedule(
                navigateToMatchForm = { navController.navigate("MatchForm") },
                back = {navController.navigate("Home")}
            )
        }
        composable("PitSchedule") {
            PitSchedule (
                navigateToPitForm = { navController.navigate("PitForm") },
                back = {navController.navigate("Home")}
            )
        }
        composable("PitForm") {
            PitForm(
                back = { navController.popBackStack() },
                home = { navController.navigate("Home")}
            )
        }
        composable("MatchForm") {
            MatchForm(
                back = { navController.popBackStack() },
                home = { navController.navigate("Home")}
            )
        }
    }
}
