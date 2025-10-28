package com.team695.scoutifyapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun OTF(label: String="Enter Response", title: String, value: String, onChange: (String) -> Unit,
        keyboardType: KeyboardType= KeyboardType.Text) {
    Text(title)
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
    Spacer(modifier = Modifier.height(12.dp))
}
@Composable
fun OTF(
    label: String = "Enter Response",
    title: String,
    value: String,
    onChange: (String) -> Unit,
    pattern: Regex,
    focusedLeftYet: Boolean,
    onFocusUpdate: (Boolean) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Text(title)
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = {
            if (!focusedLeftYet || (value.matches(pattern) && value.isNotEmpty())) {
                Text(label)
            } else {
                Text("Invalid input", color = MaterialTheme.colorScheme.error)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    onFocusUpdate(true) // focus started
                } else if (!focusState.isFocused && focusedLeftYet.not()) {
                    onFocusUpdate(false) // focus left
                }
            },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
    Spacer(modifier = Modifier.height(12.dp))
}
