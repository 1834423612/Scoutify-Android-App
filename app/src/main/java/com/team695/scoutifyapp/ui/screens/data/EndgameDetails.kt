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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team695.scoutifyapp.data.types.ENDGAME_END_TIME
import com.team695.scoutifyapp.data.types.GameFormState
import com.team695.scoutifyapp.data.types.SHIFT3_END_TIME
import com.team695.scoutifyapp.data.types.SHIFT4_END_TIME
import com.team695.scoutifyapp.ui.theme.AccentGreen
import com.team695.scoutifyapp.ui.theme.BlueAlliance
import com.team695.scoutifyapp.ui.theme.Border
import com.team695.scoutifyapp.ui.theme.DarkGunmetal
import com.team695.scoutifyapp.ui.theme.Gunmetal
import com.team695.scoutifyapp.ui.theme.ProgressGreen
import com.team695.scoutifyapp.ui.theme.RedAlliance
import com.team695.scoutifyapp.ui.theme.TextPrimary
import com.team695.scoutifyapp.ui.viewModels.DataViewModel
import kotlinx.coroutines.delay
import kotlin.math.min
import com.team695.scoutifyapp.R
import com.team695.scoutifyapp.ui.theme.TextPrimary


@Composable
fun EndgameDetails(
    dataViewModel: DataViewModel,
    formState: GameFormState
) {
    val currentTimer = min(
        formState.teleopTotalMilliseconds - SHIFT4_END_TIME,
        formState.teleopCachedMilliseconds
    )
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
            milliseconds = formState.gameDetails.endgameCyclingTime ?: 0,
            onClick = {
                dataViewModel.formEvent(
                    gameDetails = formState.gameDetails.copy(
                        endgameCyclingTime = (formState.gameDetails.endgameCyclingTime ?: 0) + currentTimer,
                        shift4CyclingTime = (formState.gameDetails.shift4CyclingTime ?: 0) + previousTimer
                    )
                )
                dataViewModel.resetCacheTime()
            },
        ),
        Timer(
            label = "Stockpiling Time",
            milliseconds = formState.gameDetails.endgameStockpilingTime ?: 0,
            onClick = {
                dataViewModel.formEvent(
                    gameDetails = formState.gameDetails.copy(
                        endgameStockpilingTime = (formState.gameDetails.endgameStockpilingTime ?: 0) + currentTimer,
                        shift4StockpilingTime = (formState.gameDetails.shift4StockpilingTime ?: 0) + previousTimer
                    )
                )
                dataViewModel.resetCacheTime()
            },
        ),
        Timer(
            label = "Defending Time",
            milliseconds = formState.gameDetails.endgameDefendingTime ?: 0,
            onClick = {
                dataViewModel.formEvent(
                    gameDetails = formState.gameDetails.copy(
                        endgameDefendingTime = (formState.gameDetails.endgameDefendingTime ?: 0) + currentTimer,
                        shift4DefendingTime = (formState.gameDetails.shift4DefendingTime ?: 0) + previousTimer,
                    )
                )
                dataViewModel.resetCacheTime()
            },
        ),
        Timer(
            label = "Broken Time",
            milliseconds = formState.gameDetails.endgameBrokenTime ?: 0,
            onClick = {
                dataViewModel.formEvent(
                    gameDetails = formState.gameDetails.copy(
                        endgameBrokenTime = (formState.gameDetails.endgameBrokenTime ?: 0) + currentTimer,
                        shift4BrokenTime = (formState.gameDetails.shift4BrokenTime ?: 0) + previousTimer,
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

            TopbarNoButton(
                title = "Teleop (Endgame)"
            )

            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {


                // ── Center panel ──
                TimerPanel(
                    timers = timers
                )

                // ── Right panel ──
                EndgamePanel(
                    formState = formState,
                    dataViewModel = dataViewModel
                )
            }
        }
    }
}

// ─── Endgame Panel ───────────────────────────────────────────────────────────

@Composable
private fun EndgamePanel(
    formState: GameFormState,
    dataViewModel: DataViewModel
) {
    Column(
        modifier = Modifier
            .width(220.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        // Endgame header button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, Border, RoundedCornerShape(10.dp))
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Tower",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }



        CheckboxRow (
            label = "Did Robot attempt climb?",
            isChecked = formState.gameDetails.endgameAttemptsClimb,
            onCheckedChange = { isChecked: Boolean? ->
                val nextState: Boolean = when (isChecked) {
                    null -> false
                    true -> false
                    false -> true
                }

                dataViewModel.formEvent(
                    gameDetails = formState.gameDetails.copy(
                        endgameAttemptsClimb = nextState
                    )
                )
            }
        )

        CheckboxRow(
            label = "Did Robot succeed climb?",
            isChecked = formState.gameDetails.endgameClimbSuccess,
            onCheckedChange = { isChecked: Boolean? ->
                val nextState: Boolean = when (isChecked) {
                    null -> false
                    true -> false
                    false -> true
                }

                dataViewModel.formEvent(
                    gameDetails = formState.gameDetails.copy(
                        endgameClimbSuccess = nextState
                    )
                )
            }
        )

        // Field map placeholder
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(if(formState.gameDetails.endgameClimbPositionFilled) DarkGunmetal else RedAlliance.copy(alpha = 0.15f))
                .border(1.dp, Border, RoundedCornerShape(14.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Space Taken",
                color = if(formState.gameDetails.endgameClimbPositionFilled) TextPrimary else RedAlliance,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.height(16.dp))
            TowerDiagram(
                formState = formState,
                dataViewModel = dataViewModel
            )
        }
    }
}

@Composable
fun TowerDiagram(
    formState: GameFormState,
    dataViewModel: DataViewModel
) {
    Box(
        modifier = Modifier
            .aspectRatio(0.85f)
    ) {
        Image(
            painter = painterResource(id = R.drawable.towerclimb), // Temporary placeholder for compilation
            contentDescription = null, // Decorative background image
            modifier = Modifier.fillMaxSize(),
            // Crop ensures the image fills the screen bounds without distorting aspect ratio
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        )

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 3x3 Grid Structure
                Column(
                    modifier = Modifier.wrapContentSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    for (level in 4 downTo 1) {
                        Box(contentAlignment = Alignment.Center) {

                            // Checkboxes for Left, Center, Right
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val positions = listOf(
                                    "L",
                                    "CL",
                                    "CR",
                                    "R"
                                ) //left, center-left, center-right, right
                                for (pos in positions) {
                                    val spaceKey = "$level:$pos,"
                                    Checkbox(
                                        colors = CheckboxDefaults.colors(
                                            // This changes the color of the border when not checked
                                            uncheckedColor = Color.White,
                                            // Optional: Ensure the checkmark and box remain visible when checked
                                            checkedColor = ProgressGreen
                                        ),
                                        checked = formState.gameDetails.endgameClimbCode?.contains(
                                            spaceKey
                                        ) ?: false,
                                        onCheckedChange = {
                                            val unchecked: Boolean = it
                                            val position = formState.gameDetails.endgameClimbCode
                                            if (unchecked) {
                                                dataViewModel.formEvent(
                                                    gameDetails = formState.gameDetails.copy(
                                                        endgameClimbCode = if (position == null) spaceKey else formState.gameDetails.endgameClimbCode + spaceKey
                                                    )
                                                )
                                            }
                                            else {
                                                dataViewModel.formEvent(
                                                    gameDetails = formState.gameDetails.copy(
                                                        endgameClimbCode = formState.gameDetails.endgameClimbCode?.replaceFirst(
                                                            oldValue = spaceKey,
                                                            newValue = ""
                                                        )
                                                    )
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(if(level == 4) 6.dp else 22.dp))
                    }
                }
            }
        }
    }
}