package com.team695.scoutifyapp.ui.screens.data

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.team695.scoutifyapp.data.types.GameFormState
import com.team695.scoutifyapp.ui.theme.Deselected
import com.team695.scoutifyapp.ui.theme.Gunmetal
import com.team695.scoutifyapp.ui.theme.LightGunmetal
import com.team695.scoutifyapp.ui.viewModels.DataViewModel


@Composable
fun TimerPanel(
    timers: List<Timer>,
) {
    Column(
        modifier = Modifier
            .width(600.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Timer grid
        val rows = timers.chunked(2)
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { entry ->
                    TimerCard(entry = entry, modifier = Modifier.weight(1f))
                }
                // If only 1 item in last row, fill space
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}


@Composable
private fun TimerCard(entry: Timer, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Gunmetal)
            .border(1.dp, LightGunmetal, RoundedCornerShape(14.dp))
            .clickable{entry.onClick()}
    ) {
        Row(
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Clock icon placeholder
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .border(2.dp, entry.color.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🕐", fontSize = 16.sp)
            }

            Column {
                Text(
                    text = entry.timeLabel,
                    color = entry.color,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = entry.label,
                    color = Deselected,
                    fontSize = 12.sp
                )
            }
        }
    }
}