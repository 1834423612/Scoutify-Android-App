package com.team695.scoutifyapp.data.types

import com.team695.scoutifyapp.data.api.model.GameDetails
import kotlin.time.Duration.Companion.nanoseconds

data class GameFormState(
    // Metadata
    val matchNum: Int,
    val teamNumber: String,

    // teleop
    val teleopRunning: Boolean = false,
    val teleopSection: TeleopSection = TeleopSection.UNSTARTED,
    val teleopTotalMilliseconds: Int = 0,
    val teleopCachedMilliseconds: Int = 0,

    //  game detail object
    val gameDetails: GameDetails,

    // warning modal
    val showWarningModal: Boolean = false,
    val warningModalTitle: String = "",
    val warningModalText: String = ""

) {
    val teleopProgress: Float get() {
        when (teleopSection) {
            TeleopSection.UNSTARTED -> return 0f
            TeleopSection.TRANSITION -> return 1/7f
            TeleopSection.SHIFT1 -> return 2/7f
            TeleopSection.SHIFT2 -> return 3/7f
            TeleopSection.SHIFT3 -> return 4/7f
            TeleopSection.SHIFT4 -> return 5/7f
            TeleopSection.ENDGAME -> return 6/7f
            TeleopSection.ENDED -> return 1f
        }

    }
}