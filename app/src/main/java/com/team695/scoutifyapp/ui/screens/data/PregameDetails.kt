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
fun PregameDetails(
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
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // LEFT PANE: Scouting Controls
                Column(
                    modifier = Modifier
                        .weight(1f) // Takes up 1/3 of the screen width
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    ScoutingBubble(
                        modifier = Modifier
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = "Match Status",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // UX Pattern: Whole row is clickable for faster, error-free tapping
                        CheckboxRow (
                            label = "Robot is on field",
                            isChecked = formState.gameDetails.robotOnField,
                            onCheckedChange = { isChecked: Boolean? ->
                                val nextState: Boolean = when (isChecked) {
                                    null -> false
                                    true -> false
                                    false -> true
                                }

                                dataViewModel.formEvent(
                                    gameDetails = formState.gameDetails.copy(
                                        robotOnField = nextState
                                    )
                                )
                            }
                        )

                        CheckboxRow(
                            label = "Robot is preloaded",
                            isChecked = formState.gameDetails.robotPreloaded,
                            onCheckedChange = { isChecked: Boolean? ->
                                val nextState: Boolean = when (isChecked) {
                                    null -> false
                                    true -> false
                                    false -> true
                                }

                                dataViewModel.formEvent(
                                    gameDetails = formState.gameDetails.copy(
                                        robotPreloaded = nextState
                                    )
                                )
                            }
                        )

                    }
                }

                // RIGHT PANE: Field Canvas
                ScoutingBubble(
                    modifier = Modifier
                        .weight(2f) // Takes up 2/3 of the screen width
                        .fillMaxHeight()
                ) {
                    Text(
                        text = "Field Map",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Canvas wrapper
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF2A2A2A)), // Dark gray placeholder
                        contentAlignment = Alignment.Center
                    ) {
                        FieldCanvas()
                    }
                }
            }
        }
    }
}
@Composable
fun FieldCanvas() {
    // Standard FRC fields are roughly 54ft x 27ft (2:1 aspect ratio).
    // This Canvas scales to fit the available space while maintaining that ratio.
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .aspectRatio(2f)
    ) {
        // Right now, this just draws a styled gray box inside the canvas area as requested.
        // In the future, you can draw the starting lines, staging marks, etc., here.
        drawRoundRect(
            color = Color(0xFF3A3A3A),
            size = size,
            cornerRadius = CornerRadius(24f, 24f)
        )

        // Example of drawing a center line
        drawLine(
            color = Color(0xFF555555),
            start = androidx.compose.ui.geometry.Offset(size.width / 2, 0f),
            end = androidx.compose.ui.geometry.Offset(size.width / 2, size.height),
            strokeWidth = 8f
        )
    }
}