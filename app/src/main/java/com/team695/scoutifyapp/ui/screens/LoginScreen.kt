package com.team695.scoutifyapp.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.team695.scoutifyapp.ui.theme.ScoutifyTheme
import org.casdoor.Casdoor

open class LoginScreen : ComponentActivity() {
    private lateinit var casdoor: Casdoor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScoutifyTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    LoginScreen()
                }
            }
        }
    }
}