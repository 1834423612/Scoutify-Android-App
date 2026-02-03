package com.team695.scoutifyapp.ObjectBox

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Match(
    @Id var id: Long = 0,
    var teamNumber: Int,
)

