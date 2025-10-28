package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp

@Composable
fun RB(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = { onOptionSelected(option) }
                )
                Text(option, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
fun RB(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    otherText: String,
    onOtherTextChange: (String) -> Unit,
    label: String
) {
    Column {
        Text(label, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = { onOptionSelected(option) }
                )
                Text(option, modifier = Modifier.padding(start = 8.dp))
            }

            if (option == "Other" && selectedOption == "Other") {
                OutlinedTextField(
                    value = otherText,
                    onValueChange = onOtherTextChange,
                    label = { Text("Enter Response") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp, top = 4.dp, bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
fun RB(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    focusedLeftYet: Boolean,
    onFocusUpdate: (Boolean) -> Unit,
) {
    Column(modifier = modifier
        .onFocusChanged { focusState ->
            if (focusState.isFocused) {
                onFocusUpdate(true) // focus started
            } else if (!focusState.isFocused && focusedLeftYet.not()) {
                onFocusUpdate(false) // focus left
            }
        }
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        if (focusedLeftYet && selectedOption=="") {
            Text("Required Response", color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))

        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = { onOptionSelected(option) }
                )
                Text(option, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
fun RB(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    otherText: String,
    onOtherTextChange: (String) -> Unit,
    label: String,
    focusedLeftYet: Boolean,
    onFocusUpdate: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    onFocusUpdate(true) // focus started
                } else if (!focusState.isFocused && focusedLeftYet.not()) {
                    onFocusUpdate(false) // focus left
                }
            }
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        if (focusedLeftYet && selectedOption=="") {
            Text("Required Response", color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))

        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = { onOptionSelected(option) }
                )
                Text(option, modifier = Modifier.padding(start = 8.dp))
            }

            if (option == "Other" && selectedOption == "Other") {
                OutlinedTextField(
                    value = otherText,
                    onValueChange = onOtherTextChange,
                    label = { Text("Enter Response") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp, top = 4.dp, bottom = 8.dp)
                )
            }
        }
    }
}