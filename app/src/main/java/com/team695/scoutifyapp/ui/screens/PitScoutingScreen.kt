package com.team695.scoutifyapp.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import com.team695.scoutifyapp.data.repository.PitScoutingRepository
import com.team695.scoutifyapp.data.types.PitImageAsset
import com.team695.scoutifyapp.data.types.PitScoutingStatus
import com.team695.scoutifyapp.data.types.PitScoutingTab
import com.team695.scoutifyapp.ui.components.form.DynamicFormField
import com.team695.scoutifyapp.ui.components.form.SectionCard
import com.team695.scoutifyapp.ui.components.form.StatChip
import com.team695.scoutifyapp.ui.viewModels.PitScoutingViewModel

@Composable
fun PitScoutingScreen(
    viewModel: PitScoutingViewModel
) {
    val state by viewModel.formState.collectAsState()
    val activeTab = state.activeTab
    var newTabTeamNumber by rememberSaveable { mutableStateOf("") }

    val fullRobotImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        viewModel.addImages(PitScoutingRepository.FULL_ROBOT_BUCKET, uris)
    }
    val driveTrainImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        viewModel.addImages(PitScoutingRepository.DRIVE_TRAIN_BUCKET, uris)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F7FB))
    ) {
        HeroHeader()

        state.syncBanner?.takeIf { it.isNotBlank() }?.let { bannerMessage ->
            SyncBanner(
                message = bannerMessage,
                onDismiss = viewModel::dismissBanner
            )
        }

        if (state.versionMismatch) {
            ResetVersionCard(onReset = viewModel::resetForVersionChange)
        }

        TabsWorkspace(
            tabs = state.tabs,
            activeTabId = activeTab?.tabId,
            newTabTeamNumber = newTabTeamNumber,
            onNewTabTeamNumberChange = { newTabTeamNumber = it },
            onCreateTab = {
                viewModel.createNewTab(newTabTeamNumber)
                newTabTeamNumber = ""
            },
            onSwitchTab = viewModel::switchToTab,
            onCloseTab = viewModel::closeTab
        )

        if (activeTab == null) {
            EmptyPitState(onCreate = { viewModel.createNewTab() })
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    SummaryBoard(tab = activeTab)
                }

                if (state.assignments.isNotEmpty()) {
                    item {
                        AssignmentBoard(
                            completedTeams = state.completedTeams,
                            assignedTeams = state.assignments.flatMap { it.assignedTeamNumbers }.distinct(),
                            onSelectTeam = viewModel::selectAssignedTeam
                        )
                    }
                }

                val sectionEntries = activeTab.fields.groupBy { if (it.section.isBlank()) "Form" else it.section }.entries.toList()
                items(sectionEntries, key = { it.key }) { entry ->
                    SectionCard(
                        title = entry.key,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                            entry.value.forEach { field ->
                                DynamicFormField(
                                    field = field,
                                    suggestions = if (field.originalIndex == 0) state.teamSuggestions else emptyList(),
                                    onTextChanged = { viewModel.updateTextField(field.originalIndex, it) },
                                    onOtherValueChanged = { viewModel.updateOtherValue(field.originalIndex, it) },
                                    onRadioSelected = { viewModel.selectRadioOption(field.originalIndex, it) },
                                    onCheckboxToggled = { viewModel.toggleCheckboxValue(field.originalIndex, it) },
                                    onSuggestionSelected = { suggestion -> viewModel.selectTeam(suggestion.teamNumber) }
                                )
                            }
                        }
                    }
                }

                item {
                    ImageBoard(
                        title = "Full Robot Images",
                        images = activeTab.images.fullRobotImages,
                        emptyText = "Capture the whole robot from angles that help strategy and inspection.",
                        onAdd = { fullRobotImageLauncher.launch("image/*") },
                        onRemove = { image -> viewModel.removeImage(PitScoutingRepository.FULL_ROBOT_BUCKET, image) }
                    )
                }

                item {
                    ImageBoard(
                        title = "Drive Train Images",
                        images = activeTab.images.driveTrainImages,
                        emptyText = "Add drivetrain close-ups for quick mechanical review.",
                        onAdd = { driveTrainImageLauncher.launch("image/*") },
                        onRemove = { image -> viewModel.removeImage(PitScoutingRepository.DRIVE_TRAIN_BUCKET, image) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            ActionBar(
                isSubmitting = state.isSubmitting,
                onClear = viewModel::clearCurrentTab,
                onSaveDraft = viewModel::saveDraft,
                onSubmit = viewModel::submitCurrentTab
            )
        }
    }
}

@Composable
private fun HeroHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0D4D92), Color(0xFF1AA3A8), Color(0xFF82D7C5))
                )
            )
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                }
                Column {
                    Text("Pit Scouting Workspace", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Dynamic template, multi-tab editing, and offline-safe submission queue.", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.86f))
                }
            }
        }
    }
}

@Composable
private fun TabsWorkspace(
    tabs: List<PitScoutingTab>,
    activeTabId: String?,
    newTabTeamNumber: String,
    onNewTabTeamNumberChange: (String) -> Unit,
    onCreateTab: () -> Unit,
    onSwitchTab: (String) -> Unit,
    onCloseTab: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Open Tabs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF16324F))
                Text("${tabs.size} active", style = MaterialTheme.typography.bodySmall, color = Color(0xFF67809A))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                tabs.forEach { tab ->
                    TabChip(tab = tab, selected = tab.tabId == activeTabId, onSelect = { onSwitchTab(tab.tabId) }, onClose = { onCloseTab(tab.tabId) })
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = newTabTeamNumber,
                    onValueChange = onNewTabTeamNumberChange,
                    modifier = Modifier.weight(1f),
                    label = { Text("Team number for new tab") },
                    singleLine = true
                )
                Button(onClick = onCreateTab) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add")
                }
            }
        }
    }
}

@Composable
private fun SummaryBoard(tab: PitScoutingTab) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF6FF))
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatChip(label = "Team", value = if (tab.teamNumber.isBlank()) "Unassigned" else tab.teamNumber, brush = Brush.linearGradient(listOf(Color(0xFF1463B8), Color(0xFF3294FF))), modifier = Modifier.weight(1f))
                StatChip(label = "Progress", value = "${(tab.completionRatio * 100).toInt()}%", brush = Brush.linearGradient(listOf(Color(0xFF0B8C8C), Color(0xFF43C6B8))), modifier = Modifier.weight(1f))
                StatChip(label = "Sync", value = syncLabel(tab.syncStatus), brush = Brush.linearGradient(listOf(Color(0xFFFB8C00), Color(0xFFFFC262))), modifier = Modifier.weight(1f))
            }
            LinearProgressIndicator(progress = tab.completionRatio, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(99.dp)))
        }
    }
}

@Composable
private fun TabChip(
    tab: PitScoutingTab,
    selected: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        onClick = onSelect,
        color = if (selected) Color(0xFF163E6C) else Color(0xFFF5F9FC),
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, top = 10.dp, bottom = 10.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(syncColor(tab.syncStatus)))
            Column {
                Text(tab.tabName, color = if (selected) Color.White else Color(0xFF17344F), fontWeight = FontWeight.SemiBold)
                Text("${(tab.completionRatio * 100).toInt()}% complete", color = if (selected) Color.White.copy(alpha = 0.72f) else Color(0xFF67809A), style = MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, contentDescription = null, tint = if (selected) Color.White else Color(0xFF67809A))
            }
        }
    }
}

@Composable
private fun AssignmentBoard(
    completedTeams: Set<String>,
    assignedTeams: List<String>,
    onSelectTeam: (String) -> Unit
) {
    SectionCard(title = "Your Assigned Teams", modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            assignedTeams.forEach { teamNumber ->
                FilterChip(
                    selected = completedTeams.contains(teamNumber),
                    onClick = { onSelectTeam(teamNumber) },
                    label = {
                        Text(if (completedTeams.contains(teamNumber)) "Team $teamNumber Completed" else "Team $teamNumber")
                    },
                    leadingIcon = {
                        Icon(
                            if (completedTeams.contains(teamNumber)) Icons.Default.CheckCircle else Icons.Default.Drafts,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ImageBoard(
    title: String,
    images: List<PitImageAsset>,
    emptyText: String,
    onAdd: () -> Unit,
    onRemove: (PitImageAsset) -> Unit
) {
    SectionCard(title = title, modifier = Modifier.padding(horizontal = 16.dp)) {
        Button(onClick = onAdd) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Images")
        }

        if (images.isEmpty()) {
            Text(emptyText, color = Color(0xFF67809A), style = MaterialTheme.typography.bodyMedium)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                images.forEach { image ->
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF6FAFD))) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(image.name, color = Color(0xFF17344F), fontWeight = FontWeight.Medium)
                                Text(
                                    if (image.uploaded) "Uploaded to cloud" else "Stored locally and waiting for upload",
                                    color = if (image.uploaded) Color(0xFF188C64) else Color(0xFFB26D00),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            IconButton(onClick = { onRemove(image) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = Color(0xFFDA4E5A))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionBar(
    isSubmitting: Boolean,
    onClear: () -> Unit,
    onSaveDraft: () -> Unit,
    onSubmit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = onClear, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Clear")
            }
            Button(onClick = onSaveDraft, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Backup, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Draft")
            }
            Button(onClick = onSubmit, modifier = Modifier.weight(1f), enabled = !isSubmitting) {
                Icon(if (isSubmitting) Icons.Default.CloudOff else Icons.Default.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isSubmitting) "Submitting" else "Submit")
            }
        }
    }
}

@Composable
private fun SyncBanner(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF17344F))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color.White)
                Text(message, color = Color.White)
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
private fun ResetVersionCard(onReset: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4E8))
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Form version changed", color = Color(0xFF7B4C00), fontWeight = FontWeight.SemiBold)
            Text("The pit scouting template was updated. Clear local tabs once so every field lines up with the latest website version.", color = Color(0xFF8E6623))
            Button(onClick = onReset) {
                Text("Clear old local tabs")
            }
        }
    }
}

@Composable
private fun EmptyPitState(onCreate: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("No pit scouting tab is open yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF16324F))
                Text("Create a tab to start drafting, collect images offline, and queue submissions safely.", color = Color(0xFF67809A))
                Button(onClick = onCreate) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Create First Tab")
                }
            }
        }
    }
}

private fun syncLabel(status: PitScoutingStatus): String {
    return when (status) {
        PitScoutingStatus.DRAFT -> "Draft"
        PitScoutingStatus.DIRTY -> "Editing"
        PitScoutingStatus.PENDING_SUBMISSION -> "Queued"
        PitScoutingStatus.SUBMITTING -> "Syncing"
        PitScoutingStatus.SUBMITTED -> "Submitted"
        PitScoutingStatus.FAILED -> "Needs Review"
    }
}

private fun syncColor(status: PitScoutingStatus): Color {
    return when (status) {
        PitScoutingStatus.DRAFT -> Color(0xFF84A3BE)
        PitScoutingStatus.DIRTY -> Color(0xFF3B82F6)
        PitScoutingStatus.PENDING_SUBMISSION -> Color(0xFFF59E0B)
        PitScoutingStatus.SUBMITTING -> Color(0xFF14B8A6)
        PitScoutingStatus.SUBMITTED -> Color(0xFF16A34A)
        PitScoutingStatus.FAILED -> Color(0xFFDC2626)
    }
}



