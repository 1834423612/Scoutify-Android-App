package com.team695.scoutifyapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*

private val LightColors = lightColorScheme(
    primary = Color(0xFF0061A4),
    secondary = Color(0xFF005B99),
    background = Color(0xFFFDFDFD)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9ACBFF),
    secondary = Color(0xFF80B2E5),
    background = Color(0xFF121212)
)

@Composable
fun ScoutifyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        typography = Typography(),
        content = content
    )
}
