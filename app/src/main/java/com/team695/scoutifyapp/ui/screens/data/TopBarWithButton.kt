package com.team695.scoutifyapp.ui.screens.data

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team695.scoutifyapp.data.types.GameFormState
import com.team695.scoutifyapp.ui.theme.Deselected
import com.team695.scoutifyapp.ui.theme.TextPrimary
import com.team695.scoutifyapp.ui.viewModels.DataViewModel

@Composable
fun TopbarWithButton(
    title: String,
    buttonLabel: String,
    buttonColor: Color,
    onButtonPressed: () -> Unit,
) {
    // Top bar
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        val btnBg by animateColorAsState(
            targetValue = buttonColor,
            animationSpec = tween(300),
            label = "btn_bg"
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(btnBg)

                .clickable{onButtonPressed()}
                .padding(horizontal = 36.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = buttonLabel,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    Spacer(Modifier.height(8.dp))

    HorizontalDivider(color = Deselected)

    Spacer(Modifier.height(16.dp))
}