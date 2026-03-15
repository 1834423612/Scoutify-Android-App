package com.team695.scoutifyapp.ui.components.form

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.InputChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.team695.scoutifyapp.data.types.FieldType
import com.team695.scoutifyapp.data.types.PitFormField
import com.team695.scoutifyapp.data.types.TeamSuggestion
import com.team695.scoutifyapp.data.types.valueAsList
import com.team695.scoutifyapp.data.types.valueAsText

@Composable
fun DynamicFormField(
    field: PitFormField,
    suggestions: List<TeamSuggestion> = emptyList(),
    onTextChanged: (String) -> Unit,
    onOtherValueChanged: (String) -> Unit,
    onRadioSelected: (String) -> Unit,
    onCheckboxToggled: (String) -> Unit,
    onSuggestionSelected: (TeamSuggestion) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionLabel(title = field.question, hint = field.description, required = field.required)

        when (field.type) {
            FieldType.TEXT, FieldType.NUMBER, FieldType.TEXTAREA, FieldType.AUTOCOMPLETE -> {
                OutlinedTextField(
                    value = field.valueAsText(),
                    onValueChange = onTextChanged,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = if (field.type == FieldType.TEXTAREA) 4 else 1,
                    maxLines = if (field.type == FieldType.TEXTAREA) 5 else 1,
                    isError = field.error != null,
                    supportingText = {
                        if (field.error != null) {
                            Text(field.error)
                        }
                    }
                )

                if (field.type == FieldType.AUTOCOMPLETE && suggestions.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        suggestions.forEach { suggestion ->
                            InputChip(
                                selected = false,
                                onClick = { onSuggestionSelected(suggestion) },
                                label = {
                                    Text(
                                        if (suggestion.teamName.isBlank()) suggestion.teamNumber
                                        else "${suggestion.teamNumber} - ${suggestion.teamName}"
                                    )
                                }
                            )
                        }
                    }
                }
            }

            FieldType.RADIO -> {
                OptionChips(
                    entries = field.optionEntries(),
                    selectedValues = setOf(field.valueAsText()),
                    onSelect = onRadioSelected
                )
            }

            FieldType.CHECKBOX -> {
                OptionChips(
                    entries = field.optionEntries(),
                    selectedValues = field.valueAsList().toSet(),
                    onSelect = onCheckboxToggled
                )
            }
        }

        if (field.usesOtherValue()) {
            OutlinedTextField(
                value = field.otherValue,
                onValueChange = onOtherValueChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Specify other") },
                isError = field.error != null && field.otherValue.isBlank()
            )
        }
    }
}

@Composable
private fun OptionChips(
    entries: List<Pair<String, String>>,
    selectedValues: Set<String>,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        entries.forEach { (label, value) ->
            FilterChip(
                selected = selectedValues.contains(value),
                onClick = { onSelect(value) },
                label = { Text(label) }
            )
        }
    }
}

private fun PitFormField.optionEntries(): List<Pair<String, String>> {
    val values = if (optionValues.isEmpty()) options else optionValues
    return values.mapIndexed { index, value ->
        val label = options.getOrNull(index) ?: value
        label to value
    }
}

private fun PitFormField.usesOtherValue(): Boolean {
    return valueAsText() == "Other" || valueAsList().contains("Other")
}
