package com.team695.scoutifyapp.data.types

import com.team695.scoutifyapp.data.api.model.GameDetails
import com.team695.scoutifyapp.ui.screens.data.EndgameDetails
import kotlin.time.Duration.Companion.nanoseconds

data class GameFormState(
    // Metadata
    val matchNum: Int,
    val teamNumber: String,

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
    val warningModalText: String = ""

) {
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

    //returns integer from 0-100
    val totalProgress: Int get() {
        return (
                (
                        gameDetails.pregameProgress +
                        gameDetails.autonProgress +
                        (if (gameDetails.teleopCompleted == true) 1 else 0) +
                        gameDetails.endgameProgress +
                        gameDetails.postgameProgress
                        )/5 * 100)
            .toInt()
    }
}