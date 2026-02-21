package com.team695.scoutifyapp.ui.screens.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.team695.scoutifyapp.data.types.GameFormState
import com.team695.scoutifyapp.ui.theme.Deselected
import com.team695.scoutifyapp.ui.viewModels.DataViewModel
import com.team695.scoutifyapp.utility.displayTime



class Timer(
    val label: String,
    val milliseconds: Int,
    val onClick: () -> Unit,
    val color: Color = Deselected,
) {
    val timeLabel: String get() {
        val minutes: Int = (milliseconds / 1000) / 60
        val seconds: Int = (milliseconds / 1000) % 60
        val hundredths: Int = (milliseconds % 1000) / 10

        return "%02d:%02d.%02d".format(minutes, seconds, hundredths)    }
}