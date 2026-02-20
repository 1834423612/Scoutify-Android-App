package com.team695.scoutifyapp.data.types

import com.team695.scoutifyapp.data.api.model.GameDetails

data class GameFormState(
    // Metadata
    val matchNum: Int,
    val teamNumber: String,

    //  Game detail object
    val gameDetails: GameDetails,
)