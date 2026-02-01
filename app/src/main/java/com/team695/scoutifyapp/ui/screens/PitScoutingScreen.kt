package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team695.scoutifyapp.ui.components.form.*
import com.team695.scoutifyapp.ui.theme.*

@Composable
fun PitScoutingScreen(
    onNavigate: (String) -> Unit
) {
    var selectedTask by remember { mutableStateOf(1) }
    var selectedTab by remember { mutableStateOf("1") }
    var selectedStatus by remember { mutableStateOf(TaskStatus.IN_PROGRESS) }

    val sampleTasks = listOf(
        Task(1, "695", "Johnson Regional", "16m", 60, TaskStatus.IN_PROGRESS),
        Task(2, "254", "Johnson Regional", "5m", 30, TaskStatus.IN_PROGRESS),
        Task(3, "1678", "Johnson Regional", "2m", 15, TaskStatus.IN_PROGRESS),
        Task(4, "118", "Johnson Regional", "--", 0, TaskStatus.INCOMPLETE),
        Task(5, "2056", "Johnson Regional", "--", 0, TaskStatus.INCOMPLETE),
        Task(9, "148", "Johnson Regional", "23m", 100, TaskStatus.DONE),
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        // Navigation Sidebar
        NavSidebar(
            currentScreen = "pit_scouting",
            onNavigate = onNavigate
        )

        // Tasks Sidebar
        TasksSidebar(
            tasks = sampleTasks.filter { it.status == selectedStatus },
            selectedTaskId = selectedTask,
            onTaskSelected = { selectedTask = it }
        )

        // Main Content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(BgPrimary)
        ) {
            // Header with tabs
            ContentHeader(
                selectedTab = selectedTab,
                onTabChange = { selectedTab = it },
                onAddTab = { },
                onCloseTab = { }
            )

            // Form Content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    FormInformationSection()
                }

                item {
                    RobotAbilitiesSection()
                }

                item {
                    RobotMeasurementsSection()
                }

                item {
                    RobotImagesSection()
                }

                item {
                    AdditionalCommentsSection()
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Action Buttons
            FormActionsBar()
        }
    }
}

@Composable
fun ContentHeader(
    selectedTab: String,
    onTabChange: (String) -> Unit,
    onAddTab: () -> Unit,
    onCloseTab: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgSecondary)
            .border(1.dp, BorderColor)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Pit Scouting Form",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Form Version: 2025.4.15_PROD_ED6",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = TextSecondary
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Team Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamTab(
                label = "Team 695",
                isSelected = selectedTab == "1",
                onClick = { onTabChange("1") },
                onClose = { onCloseTab("1") }
            )

            TeamTab(
                label = "Team 254",
                isSelected = selectedTab == "2",
                onClick = { onTabChange("2") },
                onClose = { onCloseTab("2") }
            )

            Box(
                modifier = Modifier
                    .background(BgTertiary, RoundedCornerShape(6.dp))
                    .border(1.dp, BorderColor, RoundedCornerShape(6.dp))
                    .clickable { onAddTab() }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Tab",
                        modifier = Modifier.size(14.dp),
                        tint = TextSecondary
                    )
                    Text(
                        text = "Add Tab",
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun TeamTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                if (isSelected) BgCard else BgTertiary,
                RoundedCornerShape(6.dp)
            )
            .border(
                1.dp,
                if (isSelected) AccentPrimary else BorderColor,
                RoundedCornerShape(6.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Tab",
                modifier = Modifier
                    .size(14.dp)
                    .clickable { onClose() },
                tint = TextSecondary
            )
        }
    }
}

@Composable
fun FormInformationSection() {
    FormSection(
        title = "Form Information"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column {
                Text(
                    text = "Event:",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                )
                Text(
                    text = "2025_JOHNSON",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Column {
                Text(
                    text = "Form ID:",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                )
                Text(
                    text = "7fdbba54...",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun RobotAbilitiesSection() {
    var selectedAuto by remember { mutableStateOf<String?>(null) }
    var selectedSpeed by remember { mutableStateOf<Int?>(null) }
    var selectedLocations by remember { mutableStateOf<Set<String>>(emptySet()) }

    FormSection(title = "Robot Abilities") {
        FormRow {
            FormGroup(modifier = Modifier.weight(1f)) {
                FormLabel("Mobility", required = true)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OptionItem(
                        label = "Yes",
                        selected = selectedAuto == "yes",
                        isRadio = true,
                        onClick = { selectedAuto = "yes" },
                        modifier = Modifier.weight(1f)
                    )
                    OptionItem(
                        label = "No",
                        selected = selectedAuto == "no",
                        isRadio = true,
                        onClick = { selectedAuto = "no" },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            FormGroup(modifier = Modifier.weight(1f)) {
                FormLabel("Robot Speed", required = true)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in 1..5) {
                        RatingItem(
                            label = when (i) {
                                1 -> "Slow"
                                5 -> "Fast"
                                else -> i.toString()
                            },
                            selected = selectedSpeed == i,
                            onClick = { selectedSpeed = i },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        FormRow {
            FormGroup(modifier = Modifier.weight(1f)) {
                FormLabel("Scoring Locations", required = true)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val locations = listOf("Level 1", "Level 2", "Level 3", "Level 4", "Algae Processor", "Algae Net")
                    locations.chunked(2).forEach { chunk ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            chunk.forEach { location ->
                                OptionItem(
                                    label = location,
                                    selected = selectedLocations.contains(location),
                                    isRadio = false,
                                    onClick = {
                                        selectedLocations = if (selectedLocations.contains(location)) {
                                            selectedLocations - location
                                        } else {
                                            selectedLocations + location
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RobotMeasurementsSection() {
    var weight by remember { mutableStateOf("") }
    var bumperWeight by remember { mutableStateOf("") }

    FormSection(title = "Robot Measurements") {
        FormRow {
            FormGroup(modifier = Modifier.weight(1f)) {
                FormLabel("Robot Weight (without Bumpers)", required = true, hint = "(lbs)")
                TextField(
                    value = weight,
                    onValueChange = { weight = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(BgTertiary, RoundedCornerShape(6.dp)),
                    placeholder = { Text("Enter weight", style = TextStyle(fontSize = 12.sp)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = BgTertiary,
                        unfocusedContainerColor = BgTertiary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            FormGroup(modifier = Modifier.weight(1f)) {
                FormLabel("Bumpers Weight", required = true, hint = "(lbs)")
                TextField(
                    value = bumperWeight,
                    onValueChange = { bumperWeight = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(BgTertiary, RoundedCornerShape(6.dp)),
                    placeholder = { Text("Enter bumpers weight", style = TextStyle(fontSize = 12.sp)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = BgTertiary,
                        unfocusedContainerColor = BgTertiary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        }
    }
}

@Composable
fun RobotImagesSection() {
    FormSection(title = "Robot Images") {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgTertiary, RoundedCornerShape(8.dp))
                .border(2.dp, BorderColor, RoundedCornerShape(8.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "�",
                    style = TextStyle(fontSize = 32.sp)
                )
                Text(
                    text = "Click or drag to upload images",
                    style = TextStyle(
                        fontSize = 13.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = "Supports JPG, PNG (Max 10MB each)",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                )
            }
        }
    }
}

@Composable
fun AdditionalCommentsSection() {
    var comments by remember { mutableStateOf("") }

    FormSection(title = "Additional Comments") {
        TextField(
            value = comments,
            onValueChange = { comments = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(BgTertiary, RoundedCornerShape(6.dp)),
            placeholder = { Text("Add any additional observations...", style = TextStyle(fontSize = 12.sp)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = BgTertiary,
                unfocusedContainerColor = BgTertiary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )
    }
}

@Composable
fun FormActionsBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgSecondary)
            .border(1.dp, BorderColor)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FormActionButton(
            text = "Clear Form",
            backgroundColor = AccentDanger.copy(alpha = 0.2f),
            textColor = AccentDanger,
            modifier = Modifier.weight(1f)
        )

        FormActionButton(
            text = "Save Draft",
            backgroundColor = BgCard,
            textColor = TextSecondary,
            modifier = Modifier.weight(1f)
        )

        FormActionButton(
            text = "Submit Form",
            backgroundColor = AccentPrimary.copy(alpha = 0.2f),
            textColor = AccentPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun FormActionButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .border(1.dp, textColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        )
    }
}
