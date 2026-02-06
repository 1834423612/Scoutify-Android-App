package com.team695.scoutifyapp.data.api.model.Entities

import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany

@Entity
data class Match(
    @Id var id: Long = 0,
    var teamNumber: Int,
    final val matchNumber: Int,
    //pregame
    var robotIsOnTheField: Boolean = true,
    var preload: Boolean = true,
    var startingPosition: Float, //value from 0 to 1, where 0 is the side closest to the outpost, 1 is farthest from 0

    //auton
    var autonPath: String, //sequence of instructions to recreate the auton path
    var autonClimb: String, //which boxes on the captcha menu are pressed
    var autonClimbAttempted: Boolean = false,
    var robotCrossedLineDuringAuton: Boolean = false, //redundant, but keep to make analysis easier

    //endgame
    var endgameClimb: String, //which boxes on the captcha menu are pressed
    var endgameClimbAttempted: Boolean = false,
    var shooting: Int, /*0: can't shoot | 1: can shoot | 2: shoots from anywhere
        3: shoots while moving | 4: shoots from anywhere, while moving */
    var minorFoul: Boolean,
    var majorFoul: Boolean,
    var teamStockpiled: Boolean = false,
    var stockpiled: String,
    var usedCoral: Boolean = false,
    var usedOutpost: Boolean = false,
    var usedBump: Boolean = false,
    var usedTrench: Boolean = false,
    var comments: String="",
){
    lateinit var actions: ToMany<Action>// Backlink to all actions belonging to this match
}


