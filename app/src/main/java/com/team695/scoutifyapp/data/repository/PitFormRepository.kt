package com.team695.scoutifyapp.data.repository

import com.team695.scoutifyapp.data.types.FieldType
import com.team695.scoutifyapp.data.types.PitFieldValue
import com.team695.scoutifyapp.data.types.PitFormField

class PitFormRepository {
    suspend fun getFormFields(eventCode: String): List<PitFormField> {
        return PitFormDataProvider.getDefaultFormFields()
    }

    suspend fun submitPitForm(
        eventCode: String,
        teamNumber: String,
        responses: Map<String, Any?>
    ): Result<String> {
        return Result.success("mock-form-id-${System.currentTimeMillis()}")
    }

    suspend fun saveDraft(
        eventCode: String,
        teamNumber: String,
        responses: Map<String, Any?>
    ): Result<String> {
        return Result.success("draft-saved")
    }

    suspend fun getTeamSuggestions(eventCode: String, query: String): List<String> {
        return listOf("695", "254", "1678", "118", "2056", "148")
            .filter { it.contains(query) }
    }

    private fun ApiFormField.toPitFormField(): PitFormField {
        return PitFormField(
            question = questionText,
            description = helpText,
            type = when (fieldType) {
                "text" -> FieldType.TEXT
                "textarea" -> FieldType.TEXTAREA
                "number" -> FieldType.NUMBER
                "autocomplete" -> FieldType.AUTOCOMPLETE
                "radio" -> FieldType.RADIO
                "checkbox" -> FieldType.CHECKBOX
                else -> FieldType.TEXT
            },
            options = options?.map { it.label } ?: emptyList(),
            optionValues = options?.map { it.value } ?: emptyList(),
            required = isRequired,
            originalIndex = order,
            value = when (defaultValue) {
                is List<*> -> PitFieldValue.MultiValue(defaultValue.filterIsInstance<String>())
                is String -> if (defaultValue.isBlank()) PitFieldValue.Empty else PitFieldValue.TextValue(defaultValue)
                is Number -> PitFieldValue.TextValue(defaultValue.toString())
                else -> PitFieldValue.Empty
            }
        )
    }

    private data class ApiFormField(
        val questionText: String,
        val helpText: String?,
        val fieldType: String,
        val options: List<ApiOption>?,
        val isRequired: Boolean,
        val defaultValue: Any?,
        val order: Int
    )

    private data class ApiOption(
        val label: String,
        val value: String
    )
}
