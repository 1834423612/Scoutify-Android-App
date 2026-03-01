package com.team695.scoutifyapp.data.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class SectionType {
    PREGAME, AUTON, TELEOP, POSTGAME
}

@Parcelize
data class GameSection(
    val type: SectionType,
    var progress: Int = 0,
) : Parcelable {
    val name: String get() = type.name.lowercase().replaceFirstChar { it.uppercase() }
}