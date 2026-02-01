package com.team695.scoutifyapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.team695.scoutifyapp.ui.screens.Form2
import com.team695.scoutifyapp.ui.screens.HomeScreen
import com.team695.scoutifyapp.ui.screens.FormScreen
import com.team695.scoutifyapp.ui.screens.MainScreen
import com.team695.scoutifyapp.ui.screens.PitScoutingScreen

@Composable
fun AppNav() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                onNavigateToPitScouting = { navController.navigate("pit_scouting") }
            )
        }
        composable("home") {
            HomeScreen(
                onNavigateToForm = { navController.navigate("form") },
                onNavigateToForm2 = { navController.navigate("form2") },
                onNavigateToPitScouting = { navController.navigate("pit_scouting") },
                onNavigate = { destination ->
                    when (destination) {
                        "home" -> {} // 已经在首页
                        "pit_scouting" -> navController.navigate("pit_scouting")
                        "form" -> navController.navigate("form")
                        "form2" -> navController.navigate("form2")
                        else -> {
                            if (destination.isNotEmpty()) {
                                navController.navigate(destination)
                            }
                        }
                    }
                }
            )
        }
        composable("form") {
            FormScreen(onBack = { navController.popBackStack() })
        }
        composable("form2") {
            Form2(onBack = { navController.popBackStack() })
        }
        composable("pit_scouting") {
            PitScoutingScreen(
                onNavigate = { destination ->
                    when (destination) {
                        "home" -> navController.navigate("home") {
                            popUpTo("home") { inclusive = false }
                        }
                        "pit_scouting" -> {
                            // 已经在当前页面，不需要操作
                        }
                        "form" -> navController.navigate("form")
                        "form2" -> navController.navigate("form2")
                        else -> {
                            // 尝试导航到其他页面
                            if (destination.isNotEmpty()) {
                                navController.navigate(destination)
                            }
                        }
                    }
                }
            )
        }
    }
}
