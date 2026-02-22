package com.team695.scoutifyapp.ui.screens.data

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team695.scoutifyapp.ui.components.NullableCheckbox
import com.team695.scoutifyapp.ui.components.buttonHighlight
import com.team695.scoutifyapp.ui.theme.DarkishGunmetal
import com.team695.scoutifyapp.ui.theme.Gunmetal
import com.team695.scoutifyapp.ui.theme.RedAlliance
import com.team695.scoutifyapp.ui.theme.smallCornerRadius

@Composable
fun CheckboxRow(
    label: String,
    isChecked: Boolean?,
    onCheckedChange: (Boolean?) -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onCheckedChange(isChecked) } // Large touch target
            .background(if (isChecked == true) Gunmetal else if (isChecked == false) DarkishGunmetal else RedAlliance.copy(0.1f))
            .buttonHighlight(corner = smallCornerRadius)
            .padding(16.dp)
    ) {
        NullableCheckbox(
            state = isChecked,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            color = if (isChecked == true) Color.White else Color.LightGray,
            fontSize = 18.sp
        )
    }
}