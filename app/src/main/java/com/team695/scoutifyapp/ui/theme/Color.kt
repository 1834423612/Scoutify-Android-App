package com.team695.scoutifyapp.ui.theme

import androidx.compose.ui.graphics.Color

// Pit Scouting 深色主题 - 基于HTML设计
val BgPrimary = Color(0xFF0a1628)
val BgSecondary = Color(0xFF0f2035)
val BgTertiary = Color(0xFF162a42)
val BgCard = Color(0xFF1a3148)

val AccentPrimary = Color(0xFF4a9eff)
val AccentSecondary = Color(0xFF22c55e)
val AccentWarning = Color(0xFFf59e0b)
val AccentDanger = Color(0xFFef4444)

val TextPrimary = Color(0xFFffffff)
val TextSecondary = Color(0xFF94a3b8)
val TextMuted = Color(0xFF64748b)

val BorderColor = Color(0xFF2a4a6a)

// 保留向后兼容的别名
val Background = BgPrimary
val BackgroundSecondary = BgSecondary
val BackgroundTertiary = BgTertiary
val BackgroundCard = BgCard
val BackgroundNav = BgSecondary.copy(alpha = 0.5f)

val Accent = AccentPrimary
val AccentGreen = AccentSecondary

val Deselected = TextSecondary
val LightGunmetal = Color(0xFF434343)
val Gunmetal = Color(0xFF2D3142)
val DarkishGunmetal = Color(0xFF242832)
val DarkGunmetal = BgCard

val PaneColor = BgPrimary
val RedAlliance = Color(0xFFF87171)
val BlueAlliance = Color(0xFF60A5FA)
val DarkGreen = Color(0x70244A2A)

val SelectedItem = Color(0xFF333333)
val UnSelectedItem = Color.Transparent

val Border = BorderColor
val BorderSecondary = Color.DarkGray

val BadgeBackground = Color.LightGray
val BadgeContent = Color.Black
val BadgeBackgroundSecondary = Color.Black

val TextFieldBackground = Color.Black.copy(alpha = 0.5f)

val CommentButtonBackground = Color(0xFF444444)
