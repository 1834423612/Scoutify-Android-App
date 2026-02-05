package com.team695.scoutifyapp.ui.components.form

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team695.scoutifyapp.ui.theme.*

@Composable
fun NavSidebar(
    currentScreen: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(200.dp)
            .fillMaxHeight()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BgSecondary, BgTertiary)
                )
            )
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // User Avatar
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(AccentPrimary, Color(0xFF7c3aed))
                    ),
                    shape = CircleShape
                )
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "CL",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // User Name
        Text(
            text = "User",
            style = TextStyle(
                fontSize = 11.sp,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Navigation Items
        NavItem(
            icon = Icons.Default.Home,
            label = "Home",
            isActive = currentScreen == "home",
            onClick = { onNavigate("home") }
        )

        NavItem(
            icon = Icons.Default.Settings,
            label = "Pit Scout",
            isActive = currentScreen == "pit_scouting",
            onClick = { onNavigate("pit_scouting") }
        )

        NavItem(
            icon = Icons.Default.Home,
            label = "Upload",
            isActive = currentScreen == "upload",
            onClick = { onNavigate("upload") }
        )

        NavItem(
            icon = Icons.Default.Settings,
            label = "Settings",
            isActive = currentScreen == "settings",
            onClick = { onNavigate("settings") }
        )
    }
}

@Composable
fun NavItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (isActive) {
                    Brush.linearGradient(
                        colors = listOf(AccentPrimary, Color(0xFF3b82f6))
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(Color.Transparent, Color.Transparent)
                    )
                },
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(28.dp),
            tint = if (isActive) Color.White else TextSecondary
        )
        Text(
            text = label,
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isActive) Color.White else TextSecondary
            ),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
