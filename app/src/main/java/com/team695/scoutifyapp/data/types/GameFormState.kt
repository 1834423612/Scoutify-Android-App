package com.team695.scoutifyapp.data.types

import com.team695.scoutifyapp.data.api.model.GameDetails
import kotlin.time.Duration.Companion.nanoseconds

data class GameFormState(
    // Metadata
    val matchNum: Int,
    val teamNumber: String,

    // teleop
    val teleopRunning: Boolean = false,
    val teleopSection: TeleopSection = TeleopSection.ENDGAME,
    val teleopTotalMilliseconds: Int = 0,
    val teleopCachedMilliseconds: Int = 0,

    //  game detail object
    val gameDetails: GameDetails,

    // warning modal
    val showWarningModal: Boolean = false,
    val warningModalTitle: String = "",
    val warningModalText: String = ""

)