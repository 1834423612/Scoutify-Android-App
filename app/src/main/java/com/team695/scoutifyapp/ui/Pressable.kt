package com.team695.scoutifyapp.ui.reusables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import com.team695.scoutifyapp.ui.components.buttonHighlight
import com.team695.scoutifyapp.ui.theme.DarkishGunmetal
import com.team695.scoutifyapp.ui.theme.TextPrimary

@Composable
fun Pressable(modifier: Modifier = Modifier, corner: Dp, text: String, onClick: () -> Unit, content: @Composable () -> Unit) {

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(corner))
            .clickable {onClick()},
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkishGunmetal)
                .clip(RoundedCornerShape(corner))
                .buttonHighlight(corner),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
            
        ) {
            Text(
                text = text,
                color = TextPrimary
            )
            content()
        }
    }
}