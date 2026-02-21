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
import com.team695.scoutifyapp.data.types.SHIFT1_END_TIME
import com.team695.scoutifyapp.data.types.SHIFT4_END_TIME
import com.team695.scoutifyapp.data.types.TELEOP_TIME_THRESHOLD
import com.team695.scoutifyapp.data.types.TRANSITION_END_TIME
import com.team695.scoutifyapp.data.types.TeleopSection
import com.team695.scoutifyapp.ui.theme.AccentGreen
import com.team695.scoutifyapp.ui.theme.BlueAlliance
import com.team695.scoutifyapp.ui.theme.RedAlliance
import com.team695.scoutifyapp.ui.theme.TextPrimary
import com.team695.scoutifyapp.ui.viewModels.DataViewModel
import kotlinx.coroutines.delay
import kotlin.math.abs

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
fun TransitionDetails(
    dataViewModel: DataViewModel,
    formState: GameFormState
) {

    val timers = listOf(
        Timer(
            label = "Cached Time",
            milliseconds = formState.teleopCachedMilliseconds,
            onClick = {},
            color = RedAlliance
        ),
        Timer(
            label = "Total Time",
            milliseconds = formState.teleopTotalMilliseconds,
            onClick = {},
            color = BlueAlliance
        ),
        Timer(
            label = "Cycling Time",
            milliseconds = formState.gameDetails.transitionCyclingTime ?: 0,
            onClick = {
                dataViewModel.formEvent(
                    gameDetails = formState.gameDetails.copy(
                        transitionCyclingTime = (formState.gameDetails.transitionCyclingTime ?: 0) + formState.teleopCachedMilliseconds
                    )
                )
                dataViewModel.resetCacheTime()
            },
        ),
        Timer(
            label = "Stockpiling Time",
            milliseconds = formState.gameDetails.transitionStockpilingTime ?: 0,
            onClick = {
                dataViewModel.formEvent(
                    gameDetails = formState.gameDetails.copy(
                        transitionStockpilingTime = (formState.gameDetails.transitionStockpilingTime ?: 0) + formState.teleopCachedMilliseconds
                    )
                )
                dataViewModel.resetCacheTime()
            },
        ),
        Timer(
            label = "Defending Time",
            milliseconds = formState.gameDetails.transitionDefendingTime ?: 0,
            onClick = {
                dataViewModel.formEvent(
                    gameDetails = formState.gameDetails.copy(
                        transitionDefendingTime = (formState.gameDetails.transitionDefendingTime ?: 0) + formState.teleopCachedMilliseconds
                    )
                )
                dataViewModel.resetCacheTime()
            },
        ),
        Timer(
            label = "Broken Time",
            milliseconds = formState.gameDetails.transitionBrokenTime ?: 0,
            onClick = {
                dataViewModel.formEvent(
                    gameDetails = formState.gameDetails.copy(
                        transitionBrokenTime = (formState.gameDetails.transitionBrokenTime ?: 0) + formState.teleopCachedMilliseconds
                    )
                )
                dataViewModel.resetCacheTime()
            },
        ),
    )

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
                title = "Teleop (Transition)",
                buttonLabel = "Start Shift 1",
                buttonColor = lerp(
                    start = RedAlliance,
                    stop = AccentGreen,
                    fraction = formState.teleopCachedMilliseconds.toFloat() / TRANSITION_END_TIME
                ),
                onButtonPressed = {
                    //warn user if transition shift is not close to ending
                    if(abs(formState.teleopTotalMilliseconds - TRANSITION_END_TIME) > TELEOP_TIME_THRESHOLD) {
                        dataViewModel.toggleWarningModal(title = "Are you sure?", text = "The transition period isn't over yet")
                    }
                    else {
                        dataViewModel.setTeleopSection(teleopSection = TeleopSection.SHIFT1, teleopTotalMilliseconds = TRANSITION_END_TIME)
                    }
                },
                dataViewModel = dataViewModel,
                formState = formState
            )

            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {


                TimerPanel(
                    timers = timers
                )
            }
        }
    }
}