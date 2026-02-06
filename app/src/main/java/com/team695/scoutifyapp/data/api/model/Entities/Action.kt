package com.team695.scoutifyapp.data.api.model.Entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
data class Action(
    @Id var id: Long = 0,
    var type: ActionType,
    var timestamp: Long = 0,
) {
    lateinit var match: ToOne<Match>
}
enum class ActionType {
    Defense,
    Cycle,
    Stockpile,
    Brick
}