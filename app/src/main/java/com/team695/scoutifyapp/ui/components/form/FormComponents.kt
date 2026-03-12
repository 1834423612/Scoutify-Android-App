package com.team695.scoutifyapp.ui.components.form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team695.scoutifyapp.ui.theme.*

@Composable
fun FormSection(
    title: String,
    icon: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BgCard, RoundedCornerShape(8.dp))
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (icon != null) {
                icon()
            }
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            )
        }

        content()
    }
}

@Composable
fun FormRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        content()
    }
}

@Composable
fun FormGroup(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        content()
    }
}

@Composable
fun FormLabel(
    text: String,
    required: Boolean = false,
    hint: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            )
            if (required) {
                Text(
                    text = "*",
                    style = TextStyle(
                        fontSize = 13.sp,
                        color = AccentDanger
                    )
                )
            }
        }
        if (hint != null) {
            Text(
                text = hint,
                style = TextStyle(
                    fontSize = 11.sp,
                    color = TextSecondary
                ),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun OptionItem(
    label: String,
    selected: Boolean,
    isRadio: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp)
            .background(
                if (selected) AccentPrimary.copy(alpha = 0.15f) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                if (selected) AccentPrimary else BorderColor,
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    if (selected) AccentPrimary else Color.Transparent,
                    RoundedCornerShape(if (isRadio) 50.dp else 4.dp)
                )
                .border(
                    2.dp,
                    if (selected) AccentPrimary else BorderColor,
                    RoundedCornerShape(if (isRadio) 50.dp else 4.dp)
                )
        ) {
            if (selected && !isRadio) {
                Text(
                    text = "✓",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Text(
            text = label,
            style = TextStyle(
                fontSize = 13.sp,
                color = TextPrimary
            )
        )
    }
}

@Composable
fun RatingItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(
                if (selected) {
                    AccentPrimary.copy(alpha = 0.2f)
                } else {
                    BgTertiary
                },
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                if (selected) AccentPrimary else BorderColor,
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) AccentPrimary else TextSecondary
            )
        )
    }
}

@Composable
fun StatusBadge(
    count: Int,
    backgroundColor: Color,
    textColor: Color = Color.White
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(backgroundColor, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = count.toString(),
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
    }
}
