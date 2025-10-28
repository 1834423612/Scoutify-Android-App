package com.team695.scoutifyapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TA(label: String, input: String, onChange: (String) -> Unit) {
    Text(label)
    OutlinedTextField(
        value = input,
        onValueChange = onChange,
        label = { Text("Enter Response") },
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp), // optional: controls visible height
        singleLine = false,
    )
    Spacer(modifier = Modifier.height(12.dp))
}
