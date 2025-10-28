package com.team695.scoutifyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.team695.scoutifyapp.ui.screens.Form2
import com.team695.scoutifyapp.ui.screens.FormScreen
import com.team695.scoutifyapp.ui.screens.HomeScreen
import com.team695.scoutifyapp.ui.theme.ScoutifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScoutifyTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(onNavigateToForm = {
                                navController.navigate("form")
                            }, onNavigateToForm2 = {navController.navigate("form2")})
                        }
                        composable("form") {
                            FormScreen(onBack = { navController.popBackStack() })
                        }
                        composable("form2") {
                            Form2(onBack = { navController.popBackStack() })
                        }

                    }
                }
            }
        }
    }
}
