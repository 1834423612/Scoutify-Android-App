package com.team695.scoutifyapp.ui.screens.data

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team695.scoutifyapp.data.types.ENDGAME_END_TIME
import com.team695.scoutifyapp.data.types.GameFormState
import com.team695.scoutifyapp.data.types.TRANSITION_END_TIME
import com.team695.scoutifyapp.ui.theme.AccentGreen
import com.team695.scoutifyapp.ui.theme.BlueAlliance
import com.team695.scoutifyapp.ui.theme.RedAlliance
import com.team695.scoutifyapp.ui.theme.TextPrimary
import com.team695.scoutifyapp.ui.viewModels.DataViewModel
import kotlinx.coroutines.delay

// ─── Color Palette ──────────────────────────────────────────────────────────

private val Background    = Color(0xFF0D0D0F)
private val SurfaceDark   = Color(0xFF161618)
private val SurfaceMid    = Color(0xFF1E1E22)
private val SurfaceLight  = Color(0xFF2A2A30)
private val AccentOrange  = Color(0xFFFF6B35)
private val AccentBlue    = Color(0xFF4DAFFF)
private val AccentGreen   = Color(0xFF3DDC84)
private val TextPrimary   = Color(0xFFEEEEF0)
private val TextSecondary = Color(0xFF888899)
private val BadgeRed      = Color(0xFFE53935)
private val BorderColor   = Color(0xFF2E2E36)

// ─── Root Composable ────────────────────────────────────────────────────────

@Composable
fun StoppedDetails(
    dataViewModel: DataViewModel,
    formState: GameFormState
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {

            TeleopTopBar(
                title = "Teleop (Not Started)",
                buttonLabel = "Start Teleop",
                buttonColor = lerp(
                    start = RedAlliance,
                    stop = AccentGreen,
                    fraction = formState.teleopCachedMilliseconds.toFloat() / TRANSITION_END_TIME
                ),
                onButtonPressed = {
                    //warn user if they haven't completed Auton
                    if(formState.gameDetails.teleopProgress > 0) {
                        dataViewModel.toggleWarningModal(title = "Are you sure?", text = "Restarting teleop will reset your data.")
                    }
                    else if(formState.gameDetails.autonProgress < 1) {
                        dataViewModel.toggleWarningModal(title = "Are you sure?", text = "You haven't completed Auton yet")
                    }
                    else {
                        dataViewModel.startTeleop()
                    }
                },
                dataViewModel = dataViewModel,
                formState = formState
            )
        }
    }
}