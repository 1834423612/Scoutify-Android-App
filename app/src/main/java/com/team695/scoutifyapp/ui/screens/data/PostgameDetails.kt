package com.team695.scoutifyapp.ui.screens.data

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.Role
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
import com.team695.scoutifyapp.ui.components.NullableCheckbox
import com.team695.scoutifyapp.ui.components.buttonHighlight
import com.team695.scoutifyapp.ui.theme.AccentGreen
import com.team695.scoutifyapp.ui.theme.BlueAlliance
import com.team695.scoutifyapp.ui.theme.Border
import com.team695.scoutifyapp.ui.theme.DarkGunmetal
import com.team695.scoutifyapp.ui.theme.DarkishGunmetal
import com.team695.scoutifyapp.ui.theme.Gunmetal
import com.team695.scoutifyapp.ui.theme.LightGunmetal
import com.team695.scoutifyapp.ui.theme.RedAlliance
import com.team695.scoutifyapp.ui.theme.TextPrimary
import com.team695.scoutifyapp.ui.theme.mediumCornerRadius
import com.team695.scoutifyapp.ui.theme.smallCornerRadius
import com.team695.scoutifyapp.ui.viewModels.DataViewModel
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun PostgameDetails(
    dataViewModel: DataViewModel,
    formState: GameFormState
) {


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
                title = "Pregame",
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
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ScoutingBubble(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Text(
                        text = "Robot capabilities",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // UX Pattern: Whole row is clickable for faster, error-free tapping
                    CheckboxRow (
                        label = "Shoots from anywhere",
                        isChecked = formState.gameDetails.postgameShootAnywhere,
                        onCheckedChange = { isChecked: Boolean? ->
                            val nextState: Boolean = when (isChecked) {
                                null -> false
                                true -> false
                                false -> true
                            }

                            dataViewModel.formEvent(
                                gameDetails = formState.gameDetails.copy(
                                    postgameShootAnywhere = nextState
                                )
                            )
                        }
                    )


                    CheckboxRow(
                        label = "Shoots while moving",
                        isChecked = formState.gameDetails.postgameShootWhileMoving,
                        onCheckedChange = { isChecked: Boolean? ->
                            val nextState: Boolean = when (isChecked) {
                                null -> false
                                true -> false
                                false -> true
                            }

                            dataViewModel.formEvent(
                                gameDetails = formState.gameDetails.copy(
                                    postgameShootWhileMoving = nextState
                                )
                            )
                        }
                    )

                    CheckboxRow(
                        label = "Drives under the trench",
                        isChecked = formState.gameDetails.postgameUnderTrench,
                        onCheckedChange = { isChecked: Boolean? ->
                            val nextState: Boolean = when (isChecked) {
                                null -> false
                                true -> false
                                false -> true
                            }

                            dataViewModel.formEvent(
                                gameDetails = formState.gameDetails.copy(
                                    postgameUnderTrench = nextState
                                )
                            )
                        }
                    )

                    CheckboxRow(
                        label = "Drives over the bump",
                        isChecked = formState.gameDetails.postgameOverBump,
                        onCheckedChange = { isChecked: Boolean? ->
                            val nextState: Boolean = when (isChecked) {
                                null -> false
                                true -> false
                                false -> true
                            }

                            dataViewModel.formEvent(
                                gameDetails = formState.gameDetails.copy(
                                    postgameOverBump = nextState
                                )
                            )
                        }
                    )
                }
                ScoutingBubble(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Text(
                        text = "Robot actions",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // UX Pattern: Whole row is clickable for faster, error-free tapping
                    CheckboxRow(
                        label = "Stockpiles from neutral zone",
                        isChecked = formState.gameDetails.postgameStockpileNeutral,
                        onCheckedChange = { isChecked: Boolean? ->
                            val nextState: Boolean = when (isChecked) {
                                null -> false
                                true -> false
                                false -> true
                            }

                            dataViewModel.formEvent(
                                gameDetails = formState.gameDetails.copy(
                                    postgameStockpileNeutral = nextState
                                )
                            )
                        }
                    )

                    CheckboxRow(
                        label = "Stockpiles from Alliance Zone",
                        isChecked = formState.gameDetails.postgameStockpileAlliance,
                        onCheckedChange = { isChecked: Boolean? ->
                            val nextState: Boolean = when (isChecked) {
                                null -> false
                                true -> false
                                false -> true
                            }

                            dataViewModel.formEvent(
                                gameDetails = formState.gameDetails.copy(
                                    postgameStockpileAlliance = nextState
                                )
                            )
                        }
                    )

                    CheckboxRow(
                        label = "Stockpiles cross-court",
                        isChecked = formState.gameDetails.postgameStockpileCrossCourt,
                        onCheckedChange = { isChecked: Boolean? ->
                            val nextState: Boolean = when (isChecked) {
                                null -> false
                                true -> false
                                false -> true
                            }

                            dataViewModel.formEvent(
                                gameDetails = formState.gameDetails.copy(
                                    postgameStockpileCrossCourt = nextState
                                )
                            )
                        }
                    )

                    CheckboxRow(
                        label = "Robot feeds fuel to outpost",
                        isChecked = formState.gameDetails.postgameFeedOutpost,
                        onCheckedChange = { isChecked: Boolean? ->
                            val nextState: Boolean = when (isChecked) {
                                null -> false
                                true -> false
                                false -> true
                            }

                            dataViewModel.formEvent(
                                gameDetails = formState.gameDetails.copy(
                                    postgameFeedOutpost = nextState
                                )
                            )
                        }
                    )

                    CheckboxRow(
                        label = "Robot receives fuel from outpost",
                        isChecked = formState.gameDetails.postgameReceiveOutpost,
                        onCheckedChange = { isChecked: Boolean? ->
                            val nextState: Boolean = when (isChecked) {
                                null -> false
                                true -> false
                                false -> true
                            }

                            dataViewModel.formEvent(
                                gameDetails = formState.gameDetails.copy(
                                    postgameReceiveOutpost = nextState
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}