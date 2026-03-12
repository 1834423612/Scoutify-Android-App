package com.team695.scoutifyapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*

private val PitScoutingDarkColors = darkColorScheme(
    primary = AccentPrimary,
    secondary = AccentSecondary,
    background = BgPrimary,
    surface = BgCard,
    error = AccentDanger,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onPrimary = Color.White
)

private val LightColors = lightColorScheme(
    primary = PrimaryLight,
    secondary = SecondaryLight,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = OnPrimaryLight,
    onSurface = OnSurfaceLight,
    outline = OutlineLight,
)

@Composable
fun ScoutifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        PitScoutingDarkColors
    } else {
        lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
