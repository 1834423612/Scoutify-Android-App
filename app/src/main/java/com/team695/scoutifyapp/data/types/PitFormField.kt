package com.team695.scoutifyapp.data.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PitFormField(
    val question: String,
    val description: String? = null,
    val type: FieldType,
    val options: List<String> = emptyList(),
    val optionValues: List<String> = emptyList(),
    val required: Boolean = false,
    val originalIndex: Int,
    val section: String = "",
    val value: PitFieldValue = defaultValueFor(type),
    val otherValue: String = "",
    val error: String? = null
)

@Serializable
enum class FieldType {
    AUTOCOMPLETE,
    RADIO,
    CHECKBOX,
    NUMBER,
    TEXT,
    TEXTAREA;

    val wireValue: String
        get() = name.lowercase()
}

@Serializable
sealed interface PitFieldValue {
    @Serializable
    @SerialName("empty")
    data object Empty : PitFieldValue

    @Serializable
    @SerialName("text")
    data class TextValue(val value: String) : PitFieldValue

    @Serializable
    @SerialName("multi")
    data class MultiValue(val values: List<String>) : PitFieldValue
}

fun defaultValueFor(type: FieldType): PitFieldValue {
    return if (type == FieldType.CHECKBOX) {
        PitFieldValue.MultiValue(emptyList())
    } else {
        PitFieldValue.Empty
    }
}

fun PitFormField.valueAsText(): String {
    return when (val current = value) {
        PitFieldValue.Empty -> ""
        is PitFieldValue.MultiValue -> current.values.joinToString(", ")
        is PitFieldValue.TextValue -> current.value
    }
}

fun PitFormField.valueAsList(): List<String> {
    return when (val current = value) {
        PitFieldValue.Empty -> emptyList()
        is PitFieldValue.MultiValue -> current.values
        is PitFieldValue.TextValue -> listOf(current.value)
    }
}

fun PitFormField.withTextValue(newValue: String): PitFormField {
    val normalized = newValue.trimStart()
    return copy(
        value = if (normalized.isBlank()) PitFieldValue.Empty else PitFieldValue.TextValue(normalized),
        error = null
    )
}

fun PitFormField.withCheckboxValues(newValues: List<String>): PitFormField {
    return copy(
        value = PitFieldValue.MultiValue(newValues),
        error = null
    )
}

fun PitFormField.cleared(): PitFormField {
    return copy(
        value = defaultValueFor(type),
        otherValue = "",
        error = null
    )
}
