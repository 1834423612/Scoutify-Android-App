package com.team695.scoutifyapp.data.types

/**
 * Represents a field in a pit scouting form
 */
data class PitFormField(
    val question: String,
    val description: String? = null,
    val type: FieldType,
    val options: List<String>? = null,
    val optionValues: List<String>? = null,
    val value: Any? = null,
    val required: Boolean = false,
    val originalIndex: Int
)

/**
 * Types of form fields supported
 */
enum class FieldType {
    AUTOCOMPLETE,
    RADIO,
    CHECKBOX,
    NUMBER,
    TEXT,
    TEXTAREA
}

/**
 * Represents the value stored for different field types
 */
sealed class FieldValue {
    data class StringValue(val value: String) : FieldValue()
    data class NumberValue(val value: Double) : FieldValue()
    data class BooleanValue(val value: Boolean) : FieldValue()
    data class ListValue(val values: List<String>) : FieldValue()
    object NullValue : FieldValue()
    
    fun toAny(): Any? = when (this) {
        is StringValue -> value
        is NumberValue -> value
        is BooleanValue -> value
        is ListValue -> values
        is NullValue -> null
    }
}
