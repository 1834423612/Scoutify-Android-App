package com.team695.scoutifyapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.team695.scoutifyapp.ui.screens.PitForm
import com.team695.scoutifyapp.ui.screens.*

@Composable
fun AppNav(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "Home") {

        composable("Home") {
            MainScreen(navController)
        }

        composable("MatchForm") {
            MatchFormScreen(onBack = { navController.popBackStack() })
        }

        composable("PitForm") {
            PitForm(
                back = { navController.popBackStack() },
                home = { navController.navigate("Home") }
            )
        }

        composable("MatchDetail/{matchNumber}") { backStackEntry ->
            val matchNumber = backStackEntry.arguments?.getString("matchNumber") ?: "?"
            MatchDetailScreen(
                matchNumber = matchNumber,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
