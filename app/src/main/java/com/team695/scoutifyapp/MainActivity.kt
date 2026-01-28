package com.team695.scoutifyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.team695.scoutifyapp.data.api.ScoutifyClient
import com.team695.scoutifyapp.ui.screens.Form2
import com.team695.scoutifyapp.ui.screens.FormScreen
import com.team695.scoutifyapp.ui.screens.HomeScreen
import  com.team695.scoutifyapp.ui.screens.home.MainViewModel
import com.team695.scoutifyapp.ui.theme.ScoutifyTheme
import com.team695.scoutifyapp.ui.screens.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var viewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val matchService = ScoutifyClient.matchService
        val factory = ViewModelFactory{ MainViewModel(matchService) }

        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        lifecycleScope.launch {
            viewModel.fetchMatches()

            viewModel.matches.collectLatest {
                println("FETCHING DATA: ")
                println("DATA: ${viewModel.matches.value}")
            }
        }

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
