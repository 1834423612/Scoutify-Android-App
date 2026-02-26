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


@Composable
fun StoppedDetails(
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