package com.team695.scoutifyapp.data.types

// Starting & Preload
enum class PreloadDoubleField {
    STARTING_LOCATION
}

enum class PreloadBoolField {
    ROBOT_ON_FIELD,
    ROBOT_PRELOADED
}

// Auton
enum class AutonStringField {
    PATH,
    CLIMB_POSITION
}

enum class AutonBoolField {
    ATTEMPTS_CLIMB,
    CLIMB_SUCCESS
}

enum class AutonIntField {
    FUEL_COUNT
}

// Transition Shift
enum class TransitionIntField {
    CYCLING_TIME,
    STOCKPILING_TIME,
    DEFENDING_TIME,
    BROKEN_TIME
}

enum class TransitionBoolField {
    FIRST_ACTIVE
}

// Shifts (1, 2, 3, and 4)
enum class ShiftIntField {
    CYCLING_TIME,
    STOCKPILING_TIME,
    DEFENDING_TIME,
    BROKEN_TIME
}

// Endgame
enum class EndgameIntField {
    CYCLING_TIME,
    STOCKPILING_TIME,
    DEFENDING_TIME,
    BROKEN_TIME
}

enum class EndgameBoolField {
    ATTEMPTS_CLIMB,
    CLIMB_SUCCESS
}

enum class EndgameStringField {
    CLIMB_POSITION
}

// Teleop
enum class TeleopIntField {
    FUEL_COUNT
}
enum class TeleopBoolField {
    TELEOP_FLAG
}

// Postgame

enum class PostgameBoolField {
    SHOOT_ANYWHERE,
    SHOOT_WHILE_MOVING,
    STOCKPILE_NEUTRAL,
    STOCKPILE_ALLIANCE,
    STOCKPILE_CROSS_COURT,
    FEED_OUTPOST,
    RECEIVE_OUTPOST,
    UNDER_TRENCH,
    OVER_TRENCH
}