package com.team695.scoutifyapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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

private val PitScoutingLightColors = lightColorScheme(
    primary = Color(0xFF1463B8),
    secondary = Color(0xFF1AA3A8),
    background = Color(0xFFF2F7FB),
    surface = Color.White,
    error = AccentDanger,
    onPrimary = Color.White,
    onSurface = Color(0xFF16324F),
    onBackground = Color(0xFF16324F),
    outline = Color(0xFFD2DEE8)
)

@Composable
fun ScoutifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) PitScoutingDarkColors else PitScoutingLightColors,
        typography = ScoutifyTypography,
        content = content
    )
}

