package com.team695.scoutifyapp.ui.components.form

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CB(
    options: List<String>,
    checkedStates: List<Boolean>,
    onCheckedChange: (Int, Boolean) -> Unit,
    label: String,
    otherText: String,
    onOtherTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    ) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        options.forEachIndexed { index, option ->
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = checkedStates[index],
                        onCheckedChange = { onCheckedChange(index, it) }
                    )
                    Text(option, modifier = Modifier.padding(start = 8.dp))
                }

                if (option == "Other" && checkedStates[index]) {
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

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun CB(
    options: List<String>,
    checkedStates: List<Boolean>,
    onCheckedChange: (Int, Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        options.forEachIndexed { index, option ->
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = checkedStates[index],
                        onCheckedChange = { onCheckedChange(index, it) }
                    )
                    Text(option, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}