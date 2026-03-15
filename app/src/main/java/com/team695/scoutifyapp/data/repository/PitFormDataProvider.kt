package com.team695.scoutifyapp.data.repository

import com.team695.scoutifyapp.data.types.FieldType
import com.team695.scoutifyapp.data.types.PitFieldValue
import com.team695.scoutifyapp.data.types.PitFormField
import com.team695.scoutifyapp.data.types.withTextValue

object PitFormDataProvider {
    const val FORM_VERSION = "2026.03_PROD_ED8"

    fun getDefaultFormFields(teamNumber: String = ""): List<PitFormField> {
        val teamField = PitFormField(
            question = "Team number",
            type = FieldType.AUTOCOMPLETE,
            required = true,
            originalIndex = 0,
            section = "Robot Identity"
        )

        val fields = listOf(
            teamField,
            PitFormField(
                question = "Type of drive train",
                description = "Select the drivetrain used on the robot.",
                type = FieldType.RADIO,
                options = listOf("Tank Drive", "West Coast Drive", "Swerve Drive", "Other"),
                optionValues = listOf("Tank Drive", "West Coast Drive", "Swerve Drive", "Other"),
                required = true,
                originalIndex = 1,
                section = "Mobility"
            ),
            PitFormField(
                question = "Primary mobility capabilities",
                description = "Which field elements can your robot traverse?",
                type = FieldType.CHECKBOX,
                options = listOf("BUMP", "TRENCH", "Neither"),
                optionValues = listOf("BUMP", "TRENCH", "Neither"),
                required = true,
                originalIndex = 2,
                section = "Mobility"
            ),
            PitFormField(
                question = "FUEL acquisition methods",
                description = "How does your robot acquire FUEL?",
                type = FieldType.CHECKBOX,
                options = listOf("Floor pickup", "DEPOT", "OUTPOST (Human Player)", "Preloaded only", "Other"),
                optionValues = listOf("Floor", "DEPOT", "OUTPOST", "Preloaded", "Other"),
                required = true,
                originalIndex = 3,
                section = "Fuel Strategy"
            ),
            PitFormField(
                question = "How does your robot manipulate FUEL?",
                description = "Select the primary method used by your robot.",
                type = FieldType.CHECKBOX,
                options = listOf("Shoots FUEL", "Herd FUEL", "Does not score FUEL"),
                optionValues = listOf("Shoot", "Herd", "None"),
                required = true,
                originalIndex = 4,
                section = "Fuel Strategy"
            ),
            PitFormField(
                question = "Preferred scoring range",
                description = "From where does your robot usually score into the HUB?",
                type = FieldType.CHECKBOX,
                options = listOf("Against HUB (close range)", "Mid range", "Long range", "Adjustable range"),
                optionValues = listOf("Against HUB", "Mid", "Long", "Adjustable"),
                required = true,
                originalIndex = 5,
                section = "Fuel Strategy"
            ),
            PitFormField(
                question = "Which of these can your robot do during AUTO?",
                description = "Select all that apply during AUTO.",
                type = FieldType.CHECKBOX,
                options = listOf("Leaves ALLIANCE ZONE", "Scores FUEL in HUB", "Climbs TOWER in AUTO", "No autonomous scoring"),
                optionValues = listOf("Leaves Zone", "Auto Fuel", "Auto Climb", "None"),
                required = true,
                originalIndex = 6,
                section = "Autonomous"
            ),
            PitFormField(
                question = "TOWER climbing capability",
                description = "Which RUNGS can your robot reliably climb?",
                type = FieldType.CHECKBOX,
                options = listOf("LOW RUNG", "MID RUNG", "HIGH RUNG", "No climb"),
                optionValues = listOf("Low", "Mid", "High", "None"),
                required = true,
                originalIndex = 7,
                section = "Endgame"
            ),
            PitFormField(
                question = "Robot Weight (without bumpers)",
                description = "Weight in pounds.",
                type = FieldType.NUMBER,
                required = true,
                originalIndex = 8,
                section = "Physical Specs"
            ),
            PitFormField(
                question = "Bumpers Weight",
                description = "Weight in pounds.",
                type = FieldType.NUMBER,
                required = true,
                originalIndex = 9,
                section = "Physical Specs"
            ),
            PitFormField(
                question = "Robot Dimensions (without bumpers)",
                description = "Enter dimensions in inches. Format: Length x Width x Height (e.g. 30x28x40)",
                type = FieldType.TEXT,
                required = true,
                originalIndex = 10,
                section = "Physical Specs"
            ),
            PitFormField(
                question = "Maximum robot height when fully extended",
                description = "Height in inches.",
                type = FieldType.NUMBER,
                required = true,
                originalIndex = 11,
                section = "Physical Specs"
            ),
            PitFormField(
                question = "Drive team configuration",
                type = FieldType.RADIO,
                options = listOf("Single driver", "Driver + operator", "Other"),
                optionValues = listOf("Single", "Driver + Operator", "Other"),
                required = true,
                originalIndex = 12,
                section = "Drive Team"
            ),
            PitFormField(
                question = "Estimated hours of driver practice",
                description = "Total hours or weeks practiced.",
                type = FieldType.TEXT,
                required = true,
                originalIndex = 13,
                section = "Drive Team"
            ),
            PitFormField(
                question = "Additional comments",
                type = FieldType.TEXTAREA,
                required = false,
                originalIndex = 14,
                section = "Notes"
            )
        )

        if (teamNumber.isBlank()) {
            return fields
        }

        return fields.map { field ->
            if (field.originalIndex == 0) {
                field.copy(value = PitFieldValue.TextValue(teamNumber))
            } else {
                field
            }
        }
    }

    fun createEmptyFormFields(template: List<PitFormField> = getDefaultFormFields()): List<PitFormField> {
        return template.map { field ->
            when (field.type) {
                FieldType.CHECKBOX -> field.copy(value = PitFieldValue.MultiValue(emptyList()), otherValue = "", error = null)
                else -> field.copy(value = PitFieldValue.Empty, otherValue = "", error = null)
            }
        }
    }
}
