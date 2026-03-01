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
import com.team695.scoutifyapp.data.types.TaskStatus
import com.team695.scoutifyapp.data.types.Task

@Composable
fun PitScoutingScreen(
    viewModel: PitScoutingViewModel
) {
    val formState by viewModel.formState.collectAsState()
    val screenState by viewModel.screenState.collectAsState()
    
    var selectedStatus by remember { mutableStateOf(TaskStatus.IN_PROGRESS) }
    var newTeamNumber by remember { mutableStateOf("") }

    // Sample tasks data
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
        // Tasks Sidebar with status filter
        TasksSidebar(
            tasks = sampleTasks.filter { it.status == selectedStatus },
            selectedStatus = selectedStatus,
            onStatusChanged = { selectedStatus = it },
            allTasks = sampleTasks
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
                tabs = screenState.tabs,
                selectedTabId = screenState.selectedTabId,
                formState = formState,
                onTabSelected = { tabId -> viewModel.switchToTab(tabId) },
                onTabClosed = { tabId -> viewModel.closeTab(tabId) },
                onAddTab = { 
                    if (newTeamNumber.isNotBlank()) {
                        viewModel.createNewTab(newTeamNumber)
                        newTeamNumber = ""
                    }
                }
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
                        item {
                            FormInformationSection(
                                eventName = formState.eventName,
                                formId = formState.formId
                            )
                        }

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

            // Action Buttons with Submission Message
            FormActionsBar(
                onClearForm = { viewModel.clearForm() },
                onSaveDraft = { viewModel.saveDraft() },
                onSubmitForm = { viewModel.submitForm() }
            )

            // Submission Message Toast
            val currentTabId = screenState.selectedTabId
            if (currentTabId != null) {
                val message = screenState.submissionMessages[currentTabId]
                if (!message.isNullOrBlank()) {
                    SubmissionMessageToast(
                        message = message,
                        onDismiss = { viewModel.clearSubmissionMessage(currentTabId) }
                    )
                }
            }
        }
    }
}

@Composable
fun TasksSidebar(
    tasks: List<Task>,
    selectedStatus: TaskStatus,
    allTasks: List<Task>,
    onStatusChanged: (TaskStatus) -> Unit,
    onTaskSelected: (Int) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .width(200.dp)
            .fillMaxHeight()
            .background(BgSecondary)
            .border(1.dp, BorderColor)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Pit Scouting Progress",
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Status Filter Buttons
        StatusFilterButton(
            label = "In Progress (${allTasks.count { it.status == TaskStatus.IN_PROGRESS }})",
            isSelected = selectedStatus == TaskStatus.IN_PROGRESS,
            onClick = { onStatusChanged(TaskStatus.IN_PROGRESS) },
            color = AccentPrimary
        )

        StatusFilterButton(
            label = "Incomplete (${allTasks.count { it.status == TaskStatus.INCOMPLETE }})",
            isSelected = selectedStatus == TaskStatus.INCOMPLETE,
            onClick = { onStatusChanged(TaskStatus.INCOMPLETE) },
            color = AccentWarning
        )

        StatusFilterButton(
            label = "Completed (${allTasks.count { it.status == TaskStatus.DONE }})",
            isSelected = selectedStatus == TaskStatus.DONE,
            onClick = { onStatusChanged(TaskStatus.DONE) },
            color = AccentGreen
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Task List
        Text(
            text = "Teams",
            style = TextStyle(
                fontSize = 11.sp,
                color = TextSecondary
            ),
            modifier = Modifier.padding(top = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(tasks) { task ->
                TaskListItem(task = task, onClick = { onTaskSelected(task.id) })
            }
        }
    }
}

@Composable
fun StatusFilterButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) color.copy(alpha = 0.2f) else BgTertiary,
                RoundedCornerShape(6.dp)
            )
            .border(
                1.dp,
                if (isSelected) color else BorderColor,
                RoundedCornerShape(6.dp)
            )
            .clickable { onClick() }
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 10.sp,
                color = if (isSelected) color else TextSecondary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}

@Composable
fun TaskListItem(
    task: Task,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgCard, RoundedCornerShape(4.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Team ${task.teamNumber}",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            )
            Text(
                text = "Time: ${task.timeRemaining}",
                style = TextStyle(
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(BgTertiary, RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(task.progressPercent / 100f)
                        .background(getStatusColor(task.status), RoundedCornerShape(2.dp))
                )
            }
            Text(
                text = "${task.progressPercent}%",
                style = TextStyle(
                    fontSize = 9.sp,
                    color = TextSecondary
                )
            )
        }
    }
}

private fun getStatusColor(status: TaskStatus): Color {
    return when (status) {
        TaskStatus.IN_PROGRESS -> AccentPrimary
        TaskStatus.INCOMPLETE -> AccentWarning
        TaskStatus.DONE -> AccentGreen
    }
}

@Composable
fun ContentHeader(
    tabs: List<com.team695.scoutifyapp.data.types.PitScoutingTab>,
    selectedTabId: String?,
    formState: com.team695.scoutifyapp.data.types.PitFormState,
    onTabSelected: (String) -> Unit,
    onTabClosed: (String) -> Unit,
    onAddTab: () -> Unit
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
            tabs.forEach { tab ->
                TeamTab(
                    label = if (tab.teamNumber.isNotBlank()) "Team ${tab.teamNumber}" else "New Team",
                    isSelected = selectedTabId == tab.tabId,
                    onClick = { onTabSelected(tab.tabId) },
                    onClose = { onTabClosed(tab.tabId) }
                )
            }

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
                    .clickable(enabled = true) { onClose() },
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

@Composable
fun SubmissionMessageToast(
    message: String,
    onDismiss: () -> Unit
) {
    LaunchedEffect(message) {
        kotlinx.coroutines.delay(3000)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgSecondary.copy(alpha = 0.95f))
            .border(1.dp, BorderColor)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = TextStyle(
                fontSize = 12.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        )
    }
}


