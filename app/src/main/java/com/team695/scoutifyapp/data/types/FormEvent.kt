package com.team695.scoutifyapp.data.types

sealed class FormEvent {
    // Metadata
    data class UpdateMetadata(val matchId: Int, val teamNumber: Int) : FormEvent()

    // Preload
    data class UpdatePreloadDouble(val field: PreloadDoubleField, val value: Double?) : FormEvent()
    data class UpdatePreloadBool(val field: PreloadBoolField, val value: Boolean?) : FormEvent()

    // Auton
    data class UpdateAutonString(val field: AutonStringField, val value: String?) : FormEvent()
    data class UpdateAutonBool(val field: AutonBoolField, val value: Boolean?) : FormEvent()
    data class UpdateAutonInt(val field: AutonIntField, val value: Int?) : FormEvent()

    // Teleop
    data class UpdateTeleopInt(val field: TeleopIntField, val value: Int?) : FormEvent()
    data class UpdateTeleopBool(val field: TeleopBoolField, val value: Boolean?) : FormEvent()


    // Nested Teleop Transition
    data class UpdateTransitionInt(val field: TransitionIntField, val value: Int?) : FormEvent()
    data class UpdateTransitionBool(val field: TransitionBoolField, val value: Boolean?) : FormEvent()

    // Nested Teleop Shifts (1 through 4)
    data class UpdateShiftInt(val shiftNumber: Int, val field: ShiftIntField, val value: Int?) : FormEvent()

    // Nested Teleop Endgame
    data class UpdateEndgameInt(val field: EndgameIntField, val value: Int?) : FormEvent()
    data class UpdateEndgameBool(val field: EndgameBoolField, val value: Boolean?) : FormEvent()
    data class UpdateEndgameString(val field: EndgameStringField, val value: String?) : FormEvent()

    // Postgame
    data class UpdatePostgameBool(val field: PostgameBoolField, val value: Boolean?) : FormEvent()
}