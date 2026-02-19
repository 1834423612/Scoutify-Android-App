package com.team695.scoutifyapp.data.types

data class GameFormState(
    // Metadata
    val matchId: Int = 0,
    val teamNumber: Int = 0,

    // Game Sections
    val preload: PreloadState = PreloadState(),
    val auton: AutonState = AutonState(),
    val teleop: TeleopState = TeleopState(),
    val postgame: PostgameState = PostgameState()
) {
    val overallProgress: Float get() {
        val totalFilled: Int = preload.filledFields +
                auton.filledFields +
                teleop.filledFields +
                postgame.filledFields

        val totalFields: Int = preload.totalFields +
                auton.totalFields +
                teleop.totalFields +
                postgame.totalFields

        return totalFilled.toFloat() / totalFields
    }
}

data class PreloadState(
    val doubleFields: Map<PreloadDoubleField, Double?> = PreloadDoubleField.entries.associateWith { null },
    val boolFields: Map<PreloadBoolField, Boolean?> = PreloadBoolField.entries.associateWith { null }
) {
    val filledFields: Int get() = doubleFields.count { it.value != null } + boolFields.count { it.value != null }
    val totalFields: Int get() = doubleFields.entries.size + boolFields.entries.size
    val progress: Float get() = filledFields.toFloat() / totalFields
}

data class AutonState(
    val stringFields: Map<AutonStringField, String?> = AutonStringField.entries.associateWith { null },
    val boolFields: Map<AutonBoolField, Boolean?> = AutonBoolField.entries.associateWith { null },
    val intFields: Map<AutonIntField, Int?> = AutonIntField.entries.associateWith { null }
) {
    val filledFields: Int get() = stringFields.count { !it.value.isNullOrBlank() } + intFields.count { it.value != null } + boolFields.count { it.value != null }
    val totalFields: Int get() = intFields.entries.size + boolFields.entries.size
    val progress: Float get() = filledFields.toFloat() / totalFields
}


data class TeleopState(
    val intFields: Map<TeleopIntField, Int?> = TeleopIntField.entries.associateWith { null },
    val boolFields: Map<TeleopBoolField, Boolean?> = TeleopBoolField.entries.associateWith { null },
    // Substates
    val transition: TransitionState = TransitionState(),
    val shift1: ShiftState = ShiftState(),
    val shift2: ShiftState = ShiftState(),
    val shift3: ShiftState = ShiftState(),
    val shift4: ShiftState = ShiftState(),
    val endgame: EndgameState = EndgameState(),
) {
    val filledFields: Int get() =
        intFields.count { it.value != null } + boolFields.count { it.value != null } +
                transition.filledFields + shift1.filledFields +
                shift2.filledFields + shift3.filledFields +
                shift4.filledFields + endgame.filledFields
    val totalFields: Int get() =
        intFields.entries.size + boolFields.entries.size +
        transition.totalFields + shift1.totalFields +
        shift2.totalFields + shift3.totalFields +
        shift4.totalFields + endgame.totalFields

    val progress: Float get() = filledFields.toFloat() / totalFields
}


data class PostgameState(
    val boolFields: Map<PostgameBoolField, Boolean?> = PostgameBoolField.entries.associateWith { null },
) {
    val filledFields: Int get() = boolFields.count { it.value != null }
    val totalFields: Int get() = boolFields.entries.size

    val progress: Float get() = filledFields.toFloat() / totalFields
}


// Teleop substates

data class TransitionState(
    val intFields: Map<TransitionIntField, Int?> = TransitionIntField.entries.associateWith { null },
    val boolFields: Map<TransitionBoolField, Boolean?> = TransitionBoolField.entries.associateWith { null }
) {
    val filledFields: Int get() = intFields.count { it.value != null } + boolFields.count { it.value != null }
    val totalFields: Int get() = intFields.entries.size + boolFields.entries.size
    val progress: Float get() = filledFields.toFloat() / totalFields
}

data class ShiftState(
    val intFields: Map<ShiftIntField, Int?> = ShiftIntField.entries.associateWith { null }
) {
    val filledFields: Int get() = intFields.count { it.value != null }
    val totalFields: Int get() = intFields.entries.size
    val progress: Float get() = filledFields.toFloat() / totalFields
}

data class EndgameState(
    val stringFields: Map<EndgameStringField, String?> = EndgameStringField.entries.associateWith { null },
    val intFields: Map<EndgameIntField, Int?> = EndgameIntField.entries.associateWith { null },
    val boolFields: Map<EndgameBoolField, Boolean?> = EndgameBoolField.entries.associateWith { null },
) {
    val filledFields: Int get() = stringFields.count { !it.value.isNullOrBlank() } + intFields.count { it.value != null } + boolFields.count { it.value != null }
    val totalFields: Int get() = intFields.entries.size + boolFields.entries.size
    val progress: Float get() = filledFields.toFloat() / totalFields
}
