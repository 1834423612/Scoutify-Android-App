package com.team695.scoutifyapp.ui.components.form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Box(modifier = Modifier.weight(1f)) {
                SectionLabel(title = field.question, hint = field.description, required = field.required)
            }
            QuestionTypeBadge(type = field.type)
        }

        when (field.type) {
            FieldType.TEXT, FieldType.NUMBER, FieldType.TEXTAREA, FieldType.AUTOCOMPLETE -> {
                OutlinedTextField(
                    value = field.valueAsText(),
                    onValueChange = onTextChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = if (field.type == FieldType.TEXTAREA) 88.dp else 48.dp),
                    minLines = if (field.type == FieldType.TEXTAREA) 2 else 1,
                    maxLines = if (field.type == FieldType.TEXTAREA) 4 else 1,
                    isError = field.error != null,
                    shape = RoundedCornerShape(14.dp),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    keyboardOptions = if (field.type == FieldType.NUMBER) {
                        KeyboardOptions(keyboardType = KeyboardType.Number)
                    } else {
                        KeyboardOptions.Default
                    },
                    supportingText = {
                        if (field.error != null) {
                            Text(field.error)
                        }
                    }
                )

                if (field.type == FieldType.AUTOCOMPLETE && suggestions.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        suggestions.forEach { suggestion ->
                            SuggestionPill(
                                label = suggestion.teamNumber,
                                sublabel = suggestion.teamName,
                                onClick = { onSuggestionSelected(suggestion) }
                            )
                        }
                    }
                }
            }

            FieldType.RADIO -> {
                SelectionModeHint(text = "Single choice")
                SelectableOptionGroup(
                    entries = field.optionEntries(),
                    selectedValues = setOf(field.valueAsText()),
                    multiSelect = false,
                    onSelect = onRadioSelected
                )
            }

            FieldType.CHECKBOX -> {
                SelectionModeHint(text = "Multiple choice")
                SelectableOptionGroup(
                    entries = field.optionEntries(),
                    selectedValues = field.valueAsList().toSet(),
                    multiSelect = true,
                    onSelect = onCheckboxToggled
                )
            }
        }

        if (field.usesOtherValue()) {
            OutlinedTextField(
                value = field.otherValue,
                onValueChange = onOtherValueChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                label = { Text("Specify other") },
                isError = field.error != null && field.otherValue.isBlank(),
                shape = RoundedCornerShape(14.dp),
                textStyle = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SelectionModeHint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = Color(0xFF42637C),
        modifier = Modifier
            .background(Color(0xFFEAF1F7), RoundedCornerShape(99.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp)
    )
}

@Composable
private fun QuestionTypeBadge(type: FieldType) {
    val (label, background, contentColor) = when (type) {
        FieldType.TEXT -> Triple("Short", Color(0xFFEFF4FA), Color(0xFF35536B))
        FieldType.NUMBER -> Triple("Number", Color(0xFFFFF1E3), Color(0xFF8F5A16))
        FieldType.TEXTAREA -> Triple("Notes", Color(0xFFE9F7F4), Color(0xFF156B5B))
        FieldType.AUTOCOMPLETE -> Triple("Lookup", Color(0xFFEAF0FF), Color(0xFF304D8F))
        FieldType.RADIO -> Triple("Single", Color(0xFFF0ECFF), Color(0xFF5B44A7))
        FieldType.CHECKBOX -> Triple("Multi", Color(0xFFE8F7EE), Color(0xFF1F7A47))
    }

    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = contentColor,
        modifier = Modifier
            .background(background, RoundedCornerShape(99.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    )
}

@Composable
private fun SuggestionPill(
    label: String,
    sublabel: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFFD6E1EB), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(label, color = Color(0xFF17344F), fontWeight = FontWeight.SemiBold)
        if (sublabel.isNotBlank()) {
            Text(sublabel, color = Color(0xFF6C8498), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun SelectableOptionGroup(
    entries: List<Pair<String, String>>,
    selectedValues: Set<String>,
    multiSelect: Boolean,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        entries.forEach { (label, value) ->
            val selected = selectedValues.contains(value)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (selected) Color(0xFFF1F8F6) else Color(0xFFF8FBFD),
                        RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (selected) Color(0xFF73B7A3) else Color(0xFFD8E3EB),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onSelect(value) }
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SelectionIndicator(selected = selected, multiSelect = multiSelect)
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(label, color = Color(0xFF17344F), fontWeight = FontWeight.Medium)
                    Text(
                        if (multiSelect) "Tap to add or remove" else "Tap to select this option",
                        color = Color(0xFF6E879A),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = if (selected) "Selected" else if (multiSelect) "Optional" else "Pick one",
                    color = if (selected) Color(0xFF1B7B62) else Color(0xFF7A90A2),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun SelectionIndicator(selected: Boolean, multiSelect: Boolean) {
    if (multiSelect) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(
                    if (selected) Color(0xFF1B7B62) else Color.Transparent,
                    RoundedCornerShape(6.dp)
                )
                .border(1.dp, if (selected) Color(0xFF1B7B62) else Color(0xFF9AB0C0), RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(Color.White, RoundedCornerShape(2.dp))
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .size(18.dp)
                .border(1.dp, if (selected) Color(0xFF2A6DB0) else Color(0xFF9AB0C0), CircleShape)
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFF2A6DB0), CircleShape))
            }
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
