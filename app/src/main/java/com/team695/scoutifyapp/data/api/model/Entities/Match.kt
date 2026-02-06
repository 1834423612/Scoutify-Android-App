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
    var teamStockpiled: Boolean = false,
    var robotIsOnTheField: Boolean = true,
    var preload: Boolean = true,
    var autonPath: String,//? how do we store this effectively
    var autonClimb: String,
    var robotCrossedLineDuringAuton: Boolean = false,//seems redundant given auton path
    var endgameClimb: String,
    var shooting: String, //positions, shoot while moving
    var stockpiled: String,
    var usedCoral: Boolean = false,
    var usedOutpost: Boolean = false,
    var usedBump: Boolean = false,
    var usedTrench: Boolean = false,
    var comments: String="",
){
    lateinit var actions: ToMany<Action>// Backlink to all actions belonging to this match
}
