package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team695.scoutifyapp.ui.components.BackgroundGradient
import com.team695.scoutifyapp.ui.components.ImageBackground
import com.team695.scoutifyapp.ui.components.buttonHighlight
import com.team695.scoutifyapp.ui.theme.*
import com.team695.scoutifyapp.ui.reusables.Pressable

@Preview
@Composable
fun CommentsScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CommentsContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsContent() {
    var selectedMatch by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // State for comments
    var red1Comment by remember { mutableStateOf("") }
    var red2Comment by remember { mutableStateOf("") }
    var red3Comment by remember { mutableStateOf("") }
    var blue1Comment by remember { mutableStateOf("") }
    var blue2Comment by remember { mutableStateOf("") }
    var blue3Comment by remember { mutableStateOf("") }

    val matches = (1..64).map { it.toString() }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF212427))
            .border(1.dp, LightGunmetal, RoundedCornerShape(8.dp))
    ) {
        ImageBackground(x = -1445f, y = 325f)
        BackgroundGradient()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Match Comments",
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .background(DarkishGunmetal)
                        .buttonHighlight(4.dp)
                ) {

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.width(180.dp)
                    ) {
                        OutlinedTextField(
                            value = if (selectedMatch.isEmpty()) "Select Match" else "Match $selectedMatch",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = Accent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                            ),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(DarkGunmetal)
                        ) {
                            matches.forEach { match ->
                                DropdownMenuItem(
                                    text = { Text("Match $match", color = TextPrimary) },
                                    onClick = {
                                        selectedMatch = match
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(.1f))

                // Call API
                Pressable (
                    onClick = {
                        //uploadComments()
                    },
                    corner = 4.dp,
                    text = "Submit",
                    modifier = Modifier
                        .width(150.dp)
                        .height(55.dp)
                ) {}

            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Content Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    // Red Alliance Column
                    AllianceColumn(
                        title = "Red Alliance",
                        titleColor = RedAlliance,
                        modifier = Modifier.weight(1f)
                    ) {
                        CommentField("Red 1", red1Comment) { red1Comment = it }
                        CommentField("Red 2", red2Comment) { red2Comment = it }
                        CommentField("Red 3", red3Comment) { red3Comment = it }
                    }

                    // Blue Alliance Column
                    AllianceColumn(
                        title = "Blue Alliance",
                        titleColor = BlueAlliance,
                        modifier = Modifier.weight(1f)
                    ) {
                        CommentField("Blue 1", blue1Comment) { blue1Comment = it }
                        CommentField("Blue 2", blue2Comment) { blue2Comment = it }
                        CommentField("Blue 3", blue3Comment) { blue3Comment = it }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AllianceColumn(
    title: String,
    titleColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            color = titleColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp, start = 1.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content
        )
    }
}


@Composable
fun CommentField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Deselected,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 6.dp, start = 1.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp), // Fixed height for consistency
            shape = RoundedCornerShape(4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = Accent,
                unfocusedBorderColor = LightGunmetal,
                focusedContainerColor = DarkGunmetal.copy(alpha = 0.5f),
                unfocusedContainerColor = DarkGunmetal.copy(alpha = 0.3f),
                cursorColor = Accent
            ),
            placeholder = {
                Text(
                    text = "Enter comment for $label...",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
fun CommentsScreenPreview() {
    ScoutifyTheme {
        CommentsScreen()
    }
}
