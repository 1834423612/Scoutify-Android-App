package com.team695.scoutifyapp.data.objectBox.Entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Task(
    @Id var id: Long = 0,
    val type: Int
)