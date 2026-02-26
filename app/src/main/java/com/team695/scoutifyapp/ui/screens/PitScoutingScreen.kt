package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.team695.scoutifyapp.ui.viewModels.PitScoutingViewModel

@Composable
fun PitScoutingScreen(
    viewModel: PitScoutingViewModel
) {
    val formState by viewModel.formState.collectAsState()
    
    var selectedTask by remember { mutableStateOf(1) }
    var selectedTab by remember { mutableStateOf("1") }
    var selectedStatus by remember { mutableStateOf(TaskStatus.IN_PROGRESS) }

    // Sample tasks - in production, these would come from a repository
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
                formState = formState,
                selectedTab = selectedTab,
                onTabChange = { selectedTab = it },
                onAddTab = { },
                onCloseTab = { }
            )

            // Show loading or error state
            when {
                formState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AccentPrimary)
                    }
                }
                formState.error != null -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = formState.error ?: "An error occurred",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = AccentDanger
                            )
                        )
                    }
                }
                else -> {
                    // Form Content
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Form Information Section
                        item {
                            FormInformationSection(
                                eventName = formState.eventName,
                                formId = formState.formId
                            )
                        }

                        // Dynamic form fields
                        items(formState.fields) { field ->
                            DynamicFormField(
                                field = field,
                                value = formState.fieldValues[field.originalIndex],
                                onValueChange = { value ->
                                    viewModel.updateFieldValue(field.originalIndex, value)
                                },
                                validationError = formState.validationErrors[field.originalIndex],
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }

            // Action Buttons
            FormActionsBar(
                onClearForm = { viewModel.clearForm() },
                onSaveDraft = { 
                    val submission = viewModel.saveDraft()
                    // In production, save to database or send to API
                },
                onSubmitForm = {
                    val submission = viewModel.submitForm()
                    // In production, send to API
                }
            )
        }
    }
}

@Composable
fun ContentHeader(
    formState: com.team695.scoutifyapp.data.types.PitFormState,
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
                    text = "Form Version: ${formState.formVersion}",
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
                label = if (formState.teamNumber.isNotBlank()) "Team ${formState.teamNumber}" else "New Team",
                isSelected = selectedTab == "1",
                onClick = { onTabChange("1") },
                onClose = { onCloseTab("1") }
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
fun FormInformationSection(
    eventName: String,
    formId: String
) {
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
                    text = eventName,
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
                    text = formId.take(8) + "...",
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
fun FormActionsBar(
    onClearForm: () -> Unit,
    onSaveDraft: () -> Unit,
    onSubmitForm: () -> Unit
) {
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
            modifier = Modifier.weight(1f),
            onClick = onClearForm
        )

        FormActionButton(
            text = "Save Draft",
            backgroundColor = BgCard,
            textColor = TextSecondary,
            modifier = Modifier.weight(1f),
            onClick = onSaveDraft
        )

        FormActionButton(
            text = "Submit Form",
            backgroundColor = AccentPrimary.copy(alpha = 0.2f),
            textColor = AccentPrimary,
            modifier = Modifier.weight(1f),
            onClick = onSubmitForm
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
