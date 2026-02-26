package com.team695.scoutifyapp.data.types

/**
 * Represents the complete state of a pit scouting form
 */
data class PitFormState(
    val formId: String = "",
    val eventName: String = "",
    val formVersion: String = "",
    val teamNumber: String = "",
    val fields: List<PitFormField> = emptyList(),
    val fieldValues: Map<Int, Any?> = emptyMap(), // Maps originalIndex to value
    val isLoading: Boolean = false,
    val error: String? = null,
    val validationErrors: Map<Int, String> = emptyMap()
)

/**
 * Represents a pit scouting form submission
 */
data class PitFormSubmission(
    val formId: String,
    val eventName: String,
    val teamNumber: String,
    val responses: Map<String, Any?>, // Maps question to value
    val timestamp: Long = System.currentTimeMillis(),
    val isDraft: Boolean = false
)
