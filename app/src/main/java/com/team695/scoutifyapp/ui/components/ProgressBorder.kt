package com.team695.scoutifyapp.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.team695.scoutifyapp.ui.screens.home.borderGradient
import com.team695.scoutifyapp.ui.theme.DarkishGunmetal
import com.team695.scoutifyapp.ui.theme.ProgressGreen
import com.team695.scoutifyapp.ui.theme.mediumCornerRadius


fun Modifier.progressBorder(
    progress: Float
): Modifier {
    if(progress == 1f) {
        return this.then(
            other = Modifier
                .border(
                    width = 2f.dp,
                    color = ProgressGreen,
                    shape = RoundedCornerShape(mediumCornerRadius))
        )
    }
    else if (progress == 0f) {
        return this.then(
            other = Modifier
                .border(
                    width = 2f.dp,
                    color = DarkishGunmetal,
                    shape = RoundedCornerShape(mediumCornerRadius))
        )
    }
    return this.then(
        other = Modifier
            .border(
                width = 2f.dp,
                brush = borderGradient(progress),
                shape = RoundedCornerShape(mediumCornerRadius))
    )
}