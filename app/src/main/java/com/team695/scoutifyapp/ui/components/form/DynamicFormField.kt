package com.team695.scoutifyapp.ui.components.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team695.scoutifyapp.data.types.FieldType
import com.team695.scoutifyapp.data.types.PitFormField
import com.team695.scoutifyapp.ui.theme.*

/**
 * Renders a dynamic form field based on its type
 */
@Composable
fun DynamicFormField(
    field: PitFormField,
    value: Any?,
    onValueChange: (Any?) -> Unit,
    validationError: String? = null,
    modifier: Modifier = Modifier
) {
    FormGroup(modifier = modifier) {
        FormLabel(
            text = field.question,
            required = field.required,
            hint = field.description
        )

        when (field.type) {
            FieldType.TEXT -> {
                TextInputField(
                    value = value as? String ?: "",
                    onValueChange = { onValueChange(it) },
                    placeholder = "Enter ${field.question.lowercase()}",
                    error = validationError
                )
            }

            FieldType.TEXTAREA -> {
                TextAreaField(
                    value = value as? String ?: "",
                    onValueChange = { onValueChange(it) },
                    placeholder = "Enter ${field.question.lowercase()}",
                    error = validationError
                )
            }

            FieldType.NUMBER -> {
                NumberInputField(
                    value = value?.toString() ?: "",
                    onValueChange = { 
                        val numValue = it.toDoubleOrNull()
                        onValueChange(numValue)
                    },
                    placeholder = "Enter ${field.question.lowercase()}",
                    error = validationError
                )
            }

            FieldType.AUTOCOMPLETE -> {
                AutocompleteField(
                    value = value as? String ?: "",
                    onValueChange = { onValueChange(it) },
                    placeholder = "Enter ${field.question.lowercase()}",
                    error = validationError
                )
            }

            FieldType.RADIO -> {
                RadioGroupField(
                    options = field.options ?: emptyList(),
                    selectedValue = value as? String,
                    onValueChange = { selectedOption ->
                        val index = field.options?.indexOf(selectedOption) ?: -1
                        val actualValue = if (index >= 0) {
                            field.optionValues?.getOrNull(index) ?: selectedOption
                        } else {
                            selectedOption
                        }
                        onValueChange(actualValue)
                    },
                    error = validationError
                )
            }

            FieldType.CHECKBOX -> {
                CheckboxGroupField(
                    options = field.options ?: emptyList(),
                    selectedValues = (value as? List<*>)?.filterIsInstance<String>()?.toSet() ?: emptySet(),
                    onValueChange = { selectedOptions ->
                        val actualValues = selectedOptions.mapNotNull { selectedOption ->
                            val index = field.options?.indexOf(selectedOption) ?: -1
                            if (index >= 0) {
                                field.optionValues?.getOrNull(index) ?: selectedOption
                            } else {
                                selectedOption
                            }
                        }
                        onValueChange(actualValues)
                    },
                    error = validationError
                )
            }
        }

        // Show validation error
        if (validationError != null) {
            Text(
                text = validationError,
                style = TextStyle(
                    fontSize = 11.sp,
                    color = AccentDanger
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun TextInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String?
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .background(BgTertiary, RoundedCornerShape(6.dp)),
        placeholder = { 
            Text(placeholder, style = TextStyle(fontSize = 12.sp)) 
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = BgTertiary,
            unfocusedContainerColor = BgTertiary,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            errorContainerColor = BgTertiary
        ),
        isError = error != null,
        singleLine = true
    )
}

@Composable
private fun TextAreaField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String?
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(top = 8.dp)
            .background(BgTertiary, RoundedCornerShape(6.dp)),
        placeholder = { 
            Text(placeholder, style = TextStyle(fontSize = 12.sp)) 
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = BgTertiary,
            unfocusedContainerColor = BgTertiary,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            errorContainerColor = BgTertiary
        ),
        isError = error != null
    )
}

@Composable
private fun NumberInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String?
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .background(BgTertiary, RoundedCornerShape(6.dp)),
        placeholder = { 
            Text(placeholder, style = TextStyle(fontSize = 12.sp)) 
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = BgTertiary,
            unfocusedContainerColor = BgTertiary,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            errorContainerColor = BgTertiary
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = error != null,
        singleLine = true
    )
}

@Composable
private fun AutocompleteField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String?
) {
    // For now, treat as a text field
    // In production, this would have autocomplete suggestions
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .background(BgTertiary, RoundedCornerShape(6.dp)),
        placeholder = { 
            Text(placeholder, style = TextStyle(fontSize = 12.sp)) 
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = BgTertiary,
            unfocusedContainerColor = BgTertiary,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            errorContainerColor = BgTertiary
        ),
        isError = error != null,
        singleLine = true
    )
}

@Composable
private fun RadioGroupField(
    options: List<String>,
    selectedValue: String?,
    onValueChange: (String) -> Unit,
    error: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.chunked(2).forEach { chunk ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chunk.forEach { option ->
                    OptionItem(
                        label = option,
                        selected = selectedValue == option,
                        isRadio = true,
                        onClick = { onValueChange(option) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Add spacer if odd number of items in row
                if (chunk.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CheckboxGroupField(
    options: List<String>,
    selectedValues: Set<String>,
    onValueChange: (Set<String>) -> Unit,
    error: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.chunked(2).forEach { chunk ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chunk.forEach { option ->
                    OptionItem(
                        label = option,
                        selected = selectedValues.contains(option),
                        isRadio = false,
                        onClick = {
                            val newValues = if (selectedValues.contains(option)) {
                                selectedValues - option
                            } else {
                                selectedValues + option
                            }
                            onValueChange(newValues)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Add spacer if odd number of items in row
                if (chunk.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
