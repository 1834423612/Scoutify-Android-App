package com.team695.scoutifyapp.ui.screens.data

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import com.team695.scoutifyapp.ui.theme.Border
import com.team695.scoutifyapp.ui.theme.DarkGunmetal
import com.team695.scoutifyapp.ui.theme.DarkishGunmetal
import com.team695.scoutifyapp.ui.theme.Gunmetal
import com.team695.scoutifyapp.ui.theme.mediumCornerRadius
import com.team695.scoutifyapp.ui.theme.smallCornerRadius

@Composable
fun ScoutingBubble(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    // Bubble design: Rounded corners, elevated background color
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(mediumCornerRadius))
            .border(1.dp, Border, RoundedCornerShape(mediumCornerRadius))
            .background(DarkGunmetal)
            .padding(24.dp),
        content = content,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    )
}
