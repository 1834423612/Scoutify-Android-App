package com.team695.scoutifyapp.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.team695.scoutifyapp.ui.theme.Border
import com.team695.scoutifyapp.ui.theme.BorderColor
import com.team695.scoutifyapp.ui.theme.ProgressGreen
import com.team695.scoutifyapp.ui.theme.mediumCornerRadius


fun Modifier.progressBorder(
    progress: Int
): Modifier {
    if(progress == 100) {
        return this.then(
            other = Modifier
                .border(
                    width = 1.dp,
                    color = ProgressGreen,
                    shape = RoundedCornerShape(mediumCornerRadius))
        )
    }
    else if (progress == 0) {
        return this.then(
            other = Modifier
                .border(
                    width = 1.dp,
                    color = Border,
                    shape = RoundedCornerShape(mediumCornerRadius))
        )
    }
    return this.then(
        other = Modifier
            .border(
                width = 1.dp,
                brush = borderGradient(progress/100F),
                shape = RoundedCornerShape(mediumCornerRadius))
    )
}

fun borderGradient(progress: Float): Brush {
    val before = (progress - 0.1f).coerceIn(0f, 1f)
    val after = (progress + 0.1f).coerceIn(0f, 1f)

    return Brush.linearGradient(
        colorStops = arrayOf(
            0f to ProgressGreen,
            before to ProgressGreen,
            after to Border,
            1f to Border,
        )
    )
}
