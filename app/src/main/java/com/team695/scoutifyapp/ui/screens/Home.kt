package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.team695.scoutifyapp.ui.components.InfoCard

@Composable
fun Home(navigateToPitSchedule: () -> Unit, navigateToMatchSchedule: () -> Unit) {
    // MainScreen is already declared the Global Scaffold
    // Use Column layout to arrange elements vertically
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InfoCard(
            title = "Team 695",
            description = "Welcome to our scouting app!"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Use Row layout to arrange buttons horizontally
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // "Pitch Scouting" Card
            FeatureCard(
                title = "Pit Scouting",
                icon = Icons.Default.Groups,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = navigateToPitSchedule,
                modifier = Modifier.weight(1f)
            )
            // "Match Scouting" Card
            FeatureCard(
                title = "Match Scouting",
                icon = Icons.AutoMirrored.Filled.EventNote,
                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                onClick = navigateToMatchSchedule,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * A reusable and stylized feature card component.
 * @param title The title of the card.
 * @param icon The icon for the card.
 * @param backgroundColor The background color of the card.
 * @param onClick The click event handler.
 * @param modifier The modifier to be applied to the card.
 */
@Composable
private fun FeatureCard(
    title: String,
    icon: ImageVector,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(160.dp) // Set a fixed height to make cards look neater
            .clickable(onClick = onClick), // Make the entire card clickable
        shape = RoundedCornerShape(16.dp), // Larger corner radius
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Shadow effect
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer // Use the corresponding icon color from the theme
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}