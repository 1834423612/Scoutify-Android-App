package com.team695.scoutifyapp.data.types

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.team695.scoutifyapp.data.api.model.GameDetails
import com.team695.scoutifyapp.data.api.model.Stroke
import com.team695.scoutifyapp.ui.screens.data.EndgameDetails
import com.team695.scoutifyapp.ui.theme.BlueAlliance
import com.team695.scoutifyapp.ui.theme.Deselected
import com.team695.scoutifyapp.ui.theme.RedAlliance
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.times

data class GameFormState(
    // Metadata
    val teamNumber: Int,

    //  game detail object
    val gameDetails: GameDetails,
    // teleop
    val teleopRunning: Boolean = false,
    val teleopSection: TeleopSection,
    val teleopTotalMilliseconds: Int = 0,
    val teleopCachedMilliseconds: Int = 0,

    // warning modal
    val showWarningModal: Boolean = false,
    val warningModalTitle: String = "",
    val warningModalText: String = "",

    //auton canvas
    var lastDragPosition: Offset? = null,

    var utensil: String = "path",
    var currentStroke : Stroke? = null,
    var justUndid: Boolean = false,
    var paths: List<Stroke> = emptyList(),
    var undoTree: List<Stroke> = emptyList(),
    ) {

    //calculated values
    val allianceColor: Color get() {
        return when(gameDetails.alliance) {
            'R' -> RedAlliance
            'B' -> BlueAlliance
            else -> Deselected
        }

    }

    val autonProgress: Float get() {
        return if(paths.isNotEmpty()) 1f else 0f
    }

    val teleopSectionProgress: Float get() {
        if(gameDetails.teleopCompleted == true) {
            return 1f
        }

        return when (teleopSection) {
            TeleopSection.UNSTARTED -> 0f
            TeleopSection.TRANSITION -> 1/7f
            TeleopSection.SHIFT1 -> 2/7f
            TeleopSection.SHIFT2 -> 3/7f
            TeleopSection.SHIFT3 -> 4/7f
            TeleopSection.SHIFT4 -> 5/7f
            TeleopSection.ENDGAME -> 6/7f
            TeleopSection.ENDED -> 1f
        }
    }

    val teleopAndEndgameProgress: Float get() {
        return teleopSectionProgress * 0.75f + gameDetails.endgameProgress * .25f
    }

    //returns integer from 0-100
    val totalProgress: Int get() {
        val teleopProgress = if (gameDetails.teleopCompleted == true) 1 else 0
        val progress = (
                gameDetails.pregameProgress +
                autonProgress +
                teleopProgress +
                gameDetails.endgameProgress +
                gameDetails.postgameProgress
        ) / 5 * 100

        return progress.toInt()
    }
}