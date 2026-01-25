package com.team695.scoutifyapp.ui.components.app.structure

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainContent(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.weight(0.33f)) {
            TasksCard()
        }
        Box(modifier = Modifier.weight(0.67f)) {
            MatchSchedule()
        }
    }
}
