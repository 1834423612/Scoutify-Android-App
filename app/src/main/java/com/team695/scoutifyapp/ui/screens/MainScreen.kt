package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.team695.scoutifyapp.navigation.AppNav
import com.team695.scoutifyapp.ui.components.TopBarMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val back: () -> Unit = {
        navController.popBackStack()
    }

    val home: () -> Unit = {
        navController.navigate("Home") {
            launchSingleTop = true
            popUpTo("Home") { inclusive = true }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Scoutify App") },
                navigationIcon = {
                    Row {
                        IconButton(onClick = back) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                        IconButton(onClick = home) {
                            Icon(Icons.Filled.Home, contentDescription = "Home")
                        }
                    }
                },
                actions = {
                    TopBarMenu(navController = navController)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF6495ED) // Cornflower Blue
                )            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            AppNav(navController = navController)
        }
    }
}
