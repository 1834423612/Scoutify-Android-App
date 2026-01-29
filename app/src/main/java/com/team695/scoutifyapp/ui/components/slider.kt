package com.team695.scoutifyapp.ui.components
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable


@Composable
fun slider(title: String, value: Float, valueRange: ClosedFloatingPointRange<Float>, onChange: (Float) -> Unit, ) {
    Text(title)
    Slider(
        value = value,
        valueRange = valueRange,
        onValueChange = onChange
    )
    Spacer(modifier = Modifier.height(12.dp))
}