package com.team695.scoutifyapp.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.team695.scoutifyapp.data.repository.PitScoutingRepository
import com.team695.scoutifyapp.data.types.PitFieldValue
import com.team695.scoutifyapp.data.types.PitImageAsset
import com.team695.scoutifyapp.data.types.PitScoutingStatus
import com.team695.scoutifyapp.data.types.PitScoutingTab
import com.team695.scoutifyapp.ui.components.form.DynamicFormField
import com.team695.scoutifyapp.ui.components.form.SectionCard
import com.team695.scoutifyapp.ui.components.form.StatChip
import com.team695.scoutifyapp.ui.viewModels.PitScoutingViewModel
import java.io.File
import kotlin.math.roundToInt

@Composable
fun PitScoutingScreen(
    viewModel: PitScoutingViewModel
) {
    val state by viewModel.formState.collectAsState()
    val activeTab = state.activeTab
    val context = LocalContext.current
    var newTabTeamNumber by rememberSaveable { mutableStateOf("") }
    var pickerBucket by remember { mutableStateOf(PitScoutingRepository.FULL_ROBOT_BUCKET) }
    var pendingCameraBucket by remember { mutableStateOf<String?>(null) }
    var pendingCameraFile by remember { mutableStateOf<File?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        viewModel.addImages(pickerBucket, uris)
    }
    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        viewModel.addImages(pickerBucket, uris)
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val bucket = pendingCameraBucket
        val file = pendingCameraFile
        if (success && bucket != null && file != null) {
            viewModel.addImages(bucket, listOf(FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)))
        } else {
            file?.delete()
        }
        pendingCameraBucket = null
        pendingCameraFile = null
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            pendingCameraBucket?.let {
                val file = createTempImageFile(context)
                pendingCameraFile = file
                cameraLauncher.launch(FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file))
            }
        } else {
            pendingCameraFile?.delete()
            pendingCameraBucket = null
            pendingCameraFile = null
        }
    }

    fun launchGallery(bucket: String) {
        pickerBucket = bucket
        galleryLauncher.launch("image/*")
    }

    fun launchFiles(bucket: String) {
        pickerBucket = bucket
        fileLauncher.launch(arrayOf("image/*"))
    }

    fun launchCamera(bucket: String) {
        pendingCameraBucket = bucket
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val file = createTempImageFile(context)
            pendingCameraFile = file
            cameraLauncher.launch(FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file))
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        containerColor = Color(0xFFF3F6F6),
        topBar = {
            PitTopChrome(
                activeTab = activeTab,
                tabs = state.tabs,
                eventDisplayName = state.eventDisplayName,
                formVersion = state.formVersion,
                syncBanner = state.syncBanner,
                versionMismatch = state.versionMismatch,
                newTabTeamNumber = newTabTeamNumber,
                onNewTabTeamNumberChange = { newTabTeamNumber = it },
                onCreateTab = {
                    viewModel.createNewTab(newTabTeamNumber)
                    newTabTeamNumber = ""
                },
                onSwitchTab = viewModel::switchToTab,
                onCloseTab = viewModel::closeTab,
                onDismissBanner = viewModel::dismissBanner,
                onResetVersion = viewModel::resetForVersionChange
            )
        },
        bottomBar = {
            if (activeTab != null) {
                ActionDock(
                    isSubmitting = state.isSubmitting,
                    onClear = viewModel::clearCurrentTab,
                    onSaveDraft = viewModel::saveDraft,
                    onSubmit = viewModel::submitCurrentTab
                )
            }
        }
    ) { innerPadding ->
        if (activeTab == null) {
            EmptyPitState(modifier = Modifier.padding(innerPadding), onCreate = { viewModel.createNewTab() })
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color(0xFFF8FBFC), Color(0xFFF1F5F7)))),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    top = innerPadding.calculateTopPadding() + 8.dp,
                    bottom = innerPadding.calculateBottomPadding() + 12.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.assignments.isNotEmpty()) {
                    item {
                        AssignmentBoard(
                            completedTeams = state.completedTeams,
                            assignedTeams = state.assignments.flatMap { it.assignedTeamNumbers }.distinct(),
                            onSelectTeam = viewModel::selectAssignedTeam
                        )
                    }
                }

                val sectionEntries = activeTab.fields.groupBy {
                    if (it.section.isBlank()) "Form" else it.section
                }.entries.toList()

                items(sectionEntries, key = { it.key }) { entry ->
                    SectionCard(title = entry.key) {
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
                        title = "Full Robot Photos",
                        subtitle = "Wide shots for strategy and inspection review.",
                        images = activeTab.images.fullRobotImages,
                        onCamera = { launchCamera(PitScoutingRepository.FULL_ROBOT_BUCKET) },
                        onGallery = { launchGallery(PitScoutingRepository.FULL_ROBOT_BUCKET) },
                        onFiles = { launchFiles(PitScoutingRepository.FULL_ROBOT_BUCKET) },
                        onRemove = { viewModel.removeImage(PitScoutingRepository.FULL_ROBOT_BUCKET, it) }
                    )
                }

                item {
                    ImageBoard(
                        title = "Drive Train Photos",
                        subtitle = "Close mechanical detail shots.",
                        images = activeTab.images.driveTrainImages,
                        onCamera = { launchCamera(PitScoutingRepository.DRIVE_TRAIN_BUCKET) },
                        onGallery = { launchGallery(PitScoutingRepository.DRIVE_TRAIN_BUCKET) },
                        onFiles = { launchFiles(PitScoutingRepository.DRIVE_TRAIN_BUCKET) },
                        onRemove = { viewModel.removeImage(PitScoutingRepository.DRIVE_TRAIN_BUCKET, it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PitTopChrome(
    activeTab: PitScoutingTab?,
    tabs: List<PitScoutingTab>,
    eventDisplayName: String,
    formVersion: String,
    syncBanner: String?,
    versionMismatch: Boolean,
    newTabTeamNumber: String,
    onNewTabTeamNumberChange: (String) -> Unit,
    onCreateTab: () -> Unit,
    onSwitchTab: (String) -> Unit,
    onCloseTab: (String) -> Unit,
    onDismissBanner: () -> Unit,
    onResetVersion: () -> Unit
) {
    Surface(color = Color(0xFFF7FAFC), shadowElevation = 6.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color(0xFF10273F), Color(0xFF173C53), Color(0xFFF7FAFC))))
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier.size(30.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("Pit Scouting", color = Color.White, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
                            Text("$eventDisplayName | $formVersion", color = Color.White.copy(alpha = 0.72f), style = MaterialTheme.typography.bodySmall)
                        }
                        Text(
                            text = activeTab?.let { syncLabel(it.syncStatus) } ?: "No tab",
                            color = Color.White,
                            modifier = Modifier.background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(99.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    if (activeTab != null) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            StatChip("Team", activeTab.teamNumber.ifBlank { "Open" }, Brush.linearGradient(listOf(Color(0xFF1C5E96), Color(0xFF2791AD))), Modifier.weight(1f))
                            StatChip("Progress", "${(activeTab.completionRatio * 100).roundToInt()}%", Brush.linearGradient(listOf(Color(0xFF126F64), Color(0xFF2DB39B))), Modifier.weight(1f))
                            StatChip("Sync", syncLabel(activeTab.syncStatus), Brush.linearGradient(listOf(Color(0xFF80611F), Color(0xFFC09B4B))), Modifier.weight(1f))
                        }
                        LinearProgressIndicator(
                            progress = { activeTab.completionRatio },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(99.dp)),
                            color = Color(0xFF8BE1C3),
                            trackColor = Color.White.copy(alpha = 0.14f)
                        )
                    }
                }
            }
            syncBanner?.takeIf { it.isNotBlank() }?.let { message ->
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color(0xFF153A54), RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color(0xFF8BE1C3), modifier = Modifier.size(18.dp))
                        Text(message, color = Color.White, style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = onDismissBanner, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }

            if (versionMismatch) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFFFF4E4), RoundedCornerShape(16.dp)).border(1.dp, Color(0xFFE6C890), RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Template updated", color = Color(0xFF7A4B00), fontWeight = FontWeight.SemiBold)
                        Text("Reset local tabs once so fields line up with web.", color = Color(0xFF8A6427), style = MaterialTheme.typography.bodySmall)
                    }
                    FilledTonalButton(onClick = onResetVersion, shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset")
                    }
                }
            }

            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FAFC))) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Open tabs", color = Color(0xFF17344F), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                        Text("${tabs.size} active", color = Color(0xFF6E879A), style = MaterialTheme.typography.bodySmall)
                    }
                    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        tabs.forEach { tab ->
                            TabChip(
                                tab = tab,
                                selected = activeTab?.tabId == tab.tabId,
                                onSelect = { onSwitchTab(tab.tabId) },
                                onClose = { onCloseTab(tab.tabId) }
                            )
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newTabTeamNumber,
                            onValueChange = onNewTabTeamNumberChange,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            placeholder = { Text("Team #", style = MaterialTheme.typography.bodySmall) },
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                        FilledTonalButton(onClick = onCreateTab, shape = RoundedCornerShape(14.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 9.dp)) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tab")
                        }
                    }
                }
            }
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
    Surface(onClick = onSelect, color = if (selected) Color(0xFF163650) else Color.White, shape = RoundedCornerShape(14.dp)) {
        Row(
            modifier = Modifier.padding(start = 8.dp, top = 6.dp, bottom = 6.dp, end = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(syncColor(tab.syncStatus)))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(tab.tabName, color = if (selected) Color.White else Color(0xFF17344F), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                Text("${(tab.completionRatio * 100).roundToInt()}%", color = if (selected) Color.White.copy(alpha = 0.64f) else Color(0xFF6E879A), style = MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick = onClose, modifier = Modifier.size(22.dp)) {
                Icon(Icons.Default.Close, contentDescription = null, tint = if (selected) Color.White else Color(0xFF7A90A2), modifier = Modifier.size(14.dp))
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
    SectionCard(title = "Assigned Teams") {
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            assignedTeams.forEach { teamNumber ->
                val completed = completedTeams.contains(teamNumber)
                FilterChip(
                    selected = completed,
                    onClick = { onSelectTeam(teamNumber) },
                    label = { Text(if (completed) "$teamNumber done" else teamNumber) },
                    leadingIcon = {
                        Icon(if (completed) Icons.Default.CheckCircle else Icons.Default.Drafts, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                )
            }
        }
    }
}
@Composable
private fun ImageBoard(
    title: String,
    subtitle: String,
    images: List<PitImageAsset>,
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    onFiles: () -> Unit,
    onRemove: (PitImageAsset) -> Unit
) {
    SectionCard(title = title, accent = Color(0xFF0F5B7A)) {
        Text(subtitle, color = Color(0xFF667F92), style = MaterialTheme.typography.bodySmall)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRoundRect(
                        color = Color(0xFF8FB0C4),
                        topLeft = Offset.Zero,
                        size = size,
                        cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                        style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f)))
                    )
                }
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.size(42.dp).background(Color(0xFFEAF4F8), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFF135D7A))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Visual evidence", color = Color(0xFF17344F), fontWeight = FontWeight.SemiBold)
                Text("Camera, gallery, or files.", color = Color(0xFF6E879A), style = MaterialTheme.typography.bodySmall)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SourceAction("Camera", Icons.Default.CameraAlt, Modifier.weight(1f), onCamera)
                SourceAction("Gallery", Icons.Default.PhotoLibrary, Modifier.weight(1f), onGallery)
                SourceAction("Files", Icons.Default.FolderOpen, Modifier.weight(1f), onFiles)
            }
            Text("Camera permission is requested when needed. Gallery/files use the Android picker.", color = Color(0xFF7A90A2), style = MaterialTheme.typography.labelSmall)
        }

        if (images.isEmpty()) {
            Text("No images added yet.", color = Color(0xFF6E879A), style = MaterialTheme.typography.bodySmall)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                images.forEach { image ->
                    Row(
                        modifier = Modifier.fillMaxWidth().background(Color(0xFFF8FBFD), RoundedCornerShape(16.dp)).border(1.dp, Color(0xFFD7E3EC), RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(34.dp).background(Color(0xFFE9F3F9), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF155C7A), modifier = Modifier.size(18.dp))
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(image.name, color = Color(0xFF17344F), fontWeight = FontWeight.Medium)
                            Text("${formatBytes(image.size)} | ${if (image.uploaded) "Uploaded" else "Stored locally"}", color = if (image.uploaded) Color(0xFF15705D) else Color(0xFF8B651E), style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { onRemove(image) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = Color(0xFFCC4D57))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SourceAction(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFEAF1F6), contentColor = Color(0xFF17344F)),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, maxLines = 1)
    }
}

@Composable
private fun ActionDock(
    isSubmitting: Boolean,
    onClear: () -> Unit,
    onSaveDraft: () -> Unit,
    onSubmit: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp).background(Color.White.copy(alpha = 0.96f), RoundedCornerShape(20.dp)).border(1.dp, Color(0xFFD8E3EB), RoundedCornerShape(20.dp)).padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(onClick = onClear, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), contentPadding = PaddingValues(vertical = 10.dp)) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Reset")
        }
        FilledTonalButton(onClick = onSaveDraft, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), contentPadding = PaddingValues(vertical = 10.dp)) {
            Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Draft")
        }
        Button(onClick = onSubmit, modifier = Modifier.weight(1f), enabled = !isSubmitting, shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF153A54), contentColor = Color.White), contentPadding = PaddingValues(vertical = 10.dp)) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (isSubmitting) "Syncing" else "Submit")
        }
    }
}

@Composable
private fun EmptyPitState(
    modifier: Modifier = Modifier,
    onCreate: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.size(46.dp).background(Color(0xFFEAF1F7), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF153A54))
                }
                Text("No pit tab open", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF16324F))
                Text("Create a tab and start collecting data fast.", color = Color(0xFF6C8498), style = MaterialTheme.typography.bodyMedium)
                FilledTonalButton(onClick = onCreate, shape = RoundedCornerShape(14.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Create tab")
                }
            }
        }
    }
}

private fun syncLabel(status: PitScoutingStatus): String = when (status) {
    PitScoutingStatus.DRAFT -> "Draft"
    PitScoutingStatus.DIRTY -> "Editing"
    PitScoutingStatus.PENDING_SUBMISSION -> "Queued"
    PitScoutingStatus.SUBMITTING -> "Syncing"
    PitScoutingStatus.SUBMITTED -> "Submitted"
    PitScoutingStatus.FAILED -> "Review"
}

private fun syncColor(status: PitScoutingStatus): Color = when (status) {
    PitScoutingStatus.DRAFT -> Color(0xFF84A3BE)
    PitScoutingStatus.DIRTY -> Color(0xFF3B82F6)
    PitScoutingStatus.PENDING_SUBMISSION -> Color(0xFFF59E0B)
    PitScoutingStatus.SUBMITTING -> Color(0xFF14B8A6)
    PitScoutingStatus.SUBMITTED -> Color(0xFF16A34A)
    PitScoutingStatus.FAILED -> Color(0xFFDC2626)
}

private fun createTempImageFile(context: Context): File {
    val targetDir = File(context.cacheDir, "pit-captures").apply { mkdirs() }
    return File.createTempFile("pit_capture_", ".jpg", targetDir)
}

private fun formatBytes(size: Long): String {
    if (size <= 0) return "0 B"
    val kb = 1024L
    val mb = kb * 1024
    return when {
        size >= mb -> String.format("%.1f MB", size.toDouble() / mb.toDouble())
        size >= kb -> String.format("%.0f KB", size.toDouble() / kb.toDouble())
        else -> "$size B"
    }
}










