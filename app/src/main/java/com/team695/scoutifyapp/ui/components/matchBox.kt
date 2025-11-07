package com.team695.scoutifyapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MatchBox(
    matchNumber: String,
    timestamp: String,
    redTeams: List<Int>,
    blueTeams: List<Int>,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(all = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(all = 8.dp)) {
            Text(text = matchNumber, style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = timestamp, style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.weight(1f))
            Icon(VideoCamera, contentDescription = "VideoCamera")
            Spacer(modifier = Modifier.width(8.dp))
            Icon(LightBulb, contentDescription = "Lightbulb")
        }

        Spacer(modifier = Modifier.width(16.dp))

        Row {
            Text(
                text = redTeams[0].toString(),
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = redTeams[1].toString(),
                color = Color.Red,
            )
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = redTeams[2].toString(),
                color = Color.Red,
            )
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = blueTeams[0].toString(),
                color = Color.Blue,
            )
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = blueTeams[1].toString(),
                color = Color.Blue,
            )
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = blueTeams[2].toString(),
                color = Color.Blue,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

    }
}
