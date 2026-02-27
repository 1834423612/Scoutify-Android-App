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
import kotlin.math.max
import kotlin.math.min

@Composable
fun Shift1Details(
    dataViewModel: DataViewModel,
    formState: GameFormState
) {
    val currentTimer = min(formState.teleopTotalMilliseconds - TRANSITION_END_TIME, formState.teleopCachedMilliseconds)
    val previousTimer = formState.teleopCachedMilliseconds - currentTimer

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
            milliseconds = formState.gameDetails.shift1CyclingTime ?: 0,
            onClick = {
                dataViewModel.formEvent(
                    gameDetails = formState.gameDetails.copy(
                        transitionCyclingTime = (formState.gameDetails.transitionCyclingTime ?: 0) + previousTimer,
                        shift1CyclingTime = (formState.gameDetails.shift1CyclingTime ?: 0) + currentTimer
                    )
                )
                dataViewModel.resetCacheTime()
            },
        ),
        Timer(
            label = "Stockpiling Time",
            milliseconds = formState.gameDetails.shift1StockpilingTime ?: 0,
            onClick = {

                dataViewModel.formEvent(
                    gameDetails = formState.gameDetails.copy(
                        transitionStockpilingTime = (formState.gameDetails.transitionStockpilingTime ?: 0) + previousTimer,
                        shift1StockpilingTime = (formState.gameDetails.shift1StockpilingTime ?: 0) + currentTimer
                    )
                )
                dataViewModel.resetCacheTime()
            },
        ),
        Timer(
            label = "Defending Time",
            milliseconds = formState.gameDetails.shift1DefendingTime ?: 0,
            onClick = {
                dataViewModel.formEvent(
                    gameDetails = formState.gameDetails.copy(
                        transitionDefendingTime = (formState.gameDetails.transitionDefendingTime ?: 0) + previousTimer,
                        shift1DefendingTime = (formState.gameDetails.shift1DefendingTime ?: 0) + currentTimer
                    )
                )
                dataViewModel.resetCacheTime()
            },
        ),
        Timer(
            label = "Broken Time",
            milliseconds = formState.gameDetails.shift1BrokenTime ?: 0,
            onClick = {
                dataViewModel.formEvent(
                    gameDetails = formState.gameDetails.copy(
                        transitionBrokenTime = (formState.gameDetails.transitionBrokenTime ?: 0) + previousTimer,
                        shift1BrokenTime = (formState.gameDetails.shift1BrokenTime ?: 0) + currentTimer
                    )
                )
                dataViewModel.resetCacheTime()
            },
        ),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {

            TopbarWithButton(
                title = "Teleop (Shift1)",
                buttonLabel = "Start Shift 2",
                buttonColor = lerp(
                    start = RedAlliance,
                    stop = AccentGreen,
                    fraction = formState.teleopTotalMilliseconds.toFloat() / SHIFT1_END_TIME
                ),
                onButtonPressed = {
                    //warn user if shift1 shift is not close to ending
                    if(abs(formState.teleopTotalMilliseconds - SHIFT1_END_TIME) > TELEOP_TIME_THRESHOLD) {
                        dataViewModel.toggleWarningModal(title = "Are you sure?", text = "Shift 1 isn't over yet")
                    }
                    else {
                        dataViewModel.setTeleopSection(teleopSection = TeleopSection.SHIFT2, teleopTotalMilliseconds = TRANSITION_END_TIME)
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