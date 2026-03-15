package com.team695.scoutifyapp.ui.components.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.88f), RoundedCornerShape(28.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF16324F)
        )
        content()
    }
}

@Composable
fun SectionLabel(
    title: String,
    hint: String?,
    required: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF16324F)
            )
            if (required) {
                Text(
                    text = "*",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFFE24A5A)
                )
            }
        }
        if (!hint.isNullOrBlank()) {
            Text(
                text = hint,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF5D7489)
            )
        }
    }
}

@Composable
fun StatChip(
    label: String,
    value: String,
    brush: Brush,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(brush, RoundedCornerShape(22.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}
