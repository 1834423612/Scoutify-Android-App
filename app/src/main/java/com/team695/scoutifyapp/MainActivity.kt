package com.team695.scoutifyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.team695.scoutifyapp.navigation.AppNav
import com.team695.scoutifyapp.ui.theme.ScoutifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScoutifyTheme {
                Surface {
                    val navController = rememberNavController()
                    AppNav(navController = navController)
                }
            }
        }
    }
}
