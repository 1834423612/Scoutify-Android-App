package com.team695.scoutifyapp.data.repository

import com.team695.scoutifyapp.data.types.FieldType
import com.team695.scoutifyapp.data.types.PitFormField

/**
 * Provides default pit scouting form fields
 * In production, this would fetch from an API or database
 */
object PitFormDataProvider {
    
    fun getDefaultFormFields(): List<PitFormField> {
        return listOf(
            PitFormField(
                question = "Team number",
                type = FieldType.AUTOCOMPLETE,
                required = true,
                value = null,
                originalIndex = 0
            ),
            PitFormField(
                question = "Type of drive train",
                description = "Select the drivetrain used on the robot.",
                type = FieldType.RADIO,
                options = listOf(
                    "Tank Drive",
                    "West Coast Drive",
                    "Swerve Drive",
                    "Other"
                ),
                optionValues = listOf(
                    "Tank Drive",
                    "West Coast Drive",
                    "Swerve Drive",
                    "Other"
                ),
                value = null,
                required = true,
                originalIndex = 1
            ),
            PitFormField(
                question = "Primary mobility capabilities",
                description = "Which field elements can your robot traverse?",
                type = FieldType.CHECKBOX,
                options = listOf(
                    "BUMP",
                    "TRENCH",
                    "Neither"
                ),
                optionValues = listOf(
                    "BUMP",
                    "TRENCH",
                    "Neither"
                ),
                value = emptyList<String>(),
                required = true,
                originalIndex = 2
            ),
            PitFormField(
                question = "FUEL acquisition methods",
                description = "How does your robot acquire FUEL?",
                type = FieldType.CHECKBOX,
                options = listOf(
                    "Floor pickup",
                    "DEPOT",
                    "OUTPOST (Human Player)",
                    "Preloaded only",
                    "Other"
                ),
                optionValues = listOf(
                    "Floor",
                    "DEPOT",
                    "OUTPOST",
                    "Preloaded",
                    "Other"
                ),
                value = emptyList<String>(),
                required = true,
                originalIndex = 3
            ),
            PitFormField(
                question = "FUEL scoring method",
                description = "How does your robot score FUEL into the HUB?",
                type = FieldType.RADIO,
                options = listOf(
                    "Low goal / direct dump",
                    "High goal / shooting",
                    "Both",
                    "Does not score"
                ),
                optionValues = listOf(
                    "Low",
                    "High",
                    "Both",
                    "None"
                ),
                value = null,
                required = true,
                originalIndex = 4
            ),
            PitFormField(
                question = "Preferred scoring range",
                description = "From where does your robot usually score into the HUB?",
                type = FieldType.RADIO,
                options = listOf(
                    "Close range only",
                    "Mid range",
                    "Long range",
                    "Multiple ranges"
                ),
                optionValues = listOf(
                    "Close",
                    "Mid",
                    "Long",
                    "Multiple"
                ),
                value = null,
                required = true,
                originalIndex = 5
            ),
            PitFormField(
                question = "Autonomous capabilities",
                description = "Select all that apply during AUTO.",
                type = FieldType.CHECKBOX,
                options = listOf(
                    "Leaves ALLIANCE ZONE",
                    "Scores FUEL in HUB",
                    "Uses vision (AprilTags)",
                    "Climbs TOWER in AUTO",
                    "No autonomous scoring"
                ),
                optionValues = listOf(
                    "Leaves Zone",
                    "Auto Fuel",
                    "Vision",
                    "Auto Climb",
                    "None"
                ),
                value = emptyList<String>(),
                required = true,
                originalIndex = 6
            ),
            PitFormField(
                question = "TOWER climbing capability",
                description = "Which RUNGS can your robot reliably climb?",
                type = FieldType.CHECKBOX,
                options = listOf(
                    "LOW RUNG",
                    "MID RUNG",
                    "HIGH RUNG",
                    "No climb"
                ),
                optionValues = listOf(
                    "Low",
                    "Mid",
                    "High",
                    "None"
                ),
                value = emptyList<String>(),
                required = true,
                originalIndex = 7
            ),
            PitFormField(
                question = "Endgame role preference",
                description = "Primary intended role during END GAME.",
                type = FieldType.RADIO,
                options = listOf(
                    "Primary climber",
                    "Secondary climber",
                    "Fuel scoring",
                    "Defense",
                    "Support only"
                ),
                optionValues = listOf(
                    "Primary Climb",
                    "Secondary Climb",
                    "Fuel",
                    "Defense",
                    "Support"
                ),
                value = null,
                required = true,
                originalIndex = 8
            ),
            PitFormField(
                question = "Robot Weight (without bumpers)",
                description = "Weight in pounds.",
                type = FieldType.NUMBER,
                required = true,
                value = null,
                originalIndex = 9
            ),
            PitFormField(
                question = "Bumpers Weight",
                description = "Weight in pounds.",
                type = FieldType.NUMBER,
                required = true,
                value = null,
                originalIndex = 10
            ),
            PitFormField(
                question = "Robot Dimensions (without bumpers)",
                description = "Enter dimensions in inches.",
                type = FieldType.TEXT,
                required = true,
                value = null,
                originalIndex = 11
            ),
            PitFormField(
                question = "Maximum robot height when fully extended",
                description = "Height in inches.",
                type = FieldType.NUMBER,
                required = true,
                value = null,
                originalIndex = 12
            ),
            PitFormField(
                question = "Drive team configuration",
                type = FieldType.RADIO,
                options = listOf(
                    "Single driver",
                    "Driver + operator",
                    "Other"
                ),
                optionValues = listOf(
                    "Single",
                    "Driver + Operator",
                    "Other"
                ),
                value = null,
                required = true,
                originalIndex = 13
            ),
            PitFormField(
                question = "Estimated hours of driver practice",
                description = "Total hours or weeks practiced.",
                type = FieldType.TEXT,
                required = true,
                value = null,
                originalIndex = 14
            ),
            PitFormField(
                question = "Additional comments",
                type = FieldType.TEXTAREA,
                required = false,
                value = null,
                originalIndex = 15
            )
        )
    }
}
