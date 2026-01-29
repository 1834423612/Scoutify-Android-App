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
    required: Required,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Text(title)
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = {
            //println(required.focusLeftYet)
            //println(required.valid().not())
//            if(required.focusLeftYet){//&&required.valid().not(
//                Text("Invalid input", color = MaterialTheme.colorScheme.error)
//            } else Text(label)
            if(required.focusLeftYet.not()||required.valid()) Text(label)
            else Text("Invalid input", color = MaterialTheme.colorScheme.error)
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                //println("focusState")
                //println(focusState.isFocused)
                if (focusState.isFocused.not()&&required.focusStarted) required.focusLeftYet = true
                required.focusStarted = true
                //println( " → ${required.focusStarted}, ${required.focusLeftYet}, ${required.valid()}")
            }
    )
    Spacer(modifier = Modifier.height(12.dp))
}
