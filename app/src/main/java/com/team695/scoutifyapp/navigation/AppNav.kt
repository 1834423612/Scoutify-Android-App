package com.team695.scoutifyapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.team695.scoutifyapp.ui.screens.Form2
import com.team695.scoutifyapp.ui.screens.HomeScreen
import com.team695.scoutifyapp.ui.screens.FormScreen

@Composable
fun AppNav() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onNavigateToForm = { navController.navigate("form") },
                onNavigateToForm2 = { navController.navigate("form") }
            )
        }
        composable("form") {
            FormScreen(onBack = { navController.popBackStack() })
        }
        composable("form2") {
            Form2(onBack = { navController.popBackStack() })
        }
    }
}
