package com.team695.scoutifyapp.ui.modifier

import android.graphics.drawable.shapes.Shape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

fun Modifier.buttonHighlight(
    corner: Dp = 4.dp
): Modifier = this.then(
    Modifier.innerShadow(
        shape = RoundedCornerShape(corner),
        shadow = Shadow(
            radius = 0.7.dp,
            spread = 0.dp,
            color = Color(0x45FFFFFF),
            offset = DpOffset(x = 0.dp, y = 1.4.dp)
        )
    )
)