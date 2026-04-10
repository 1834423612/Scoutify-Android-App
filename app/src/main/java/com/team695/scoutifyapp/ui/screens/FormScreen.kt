package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.team695.scoutifyapp.data.repository.LocalDatabaseDebugRow
import com.team695.scoutifyapp.data.repository.LocalDatabaseDebugTable
import com.team695.scoutifyapp.ui.screens.data.TopbarWithButton
import com.team695.scoutifyapp.ui.theme.Accent
import com.team695.scoutifyapp.ui.theme.AccentSecondary
import com.team695.scoutifyapp.ui.theme.Background
import com.team695.scoutifyapp.ui.theme.DarkGunmetal
import com.team695.scoutifyapp.ui.theme.DarkishGunmetal
import com.team695.scoutifyapp.ui.theme.Deselected
import com.team695.scoutifyapp.ui.theme.LightGunmetal
import com.team695.scoutifyapp.ui.theme.TextFieldBackground
import com.team695.scoutifyapp.ui.theme.TextPrimary
import com.team695.scoutifyapp.ui.theme.TextSecondary
import com.team695.scoutifyapp.ui.viewModels.SettingsUiState
import com.team695.scoutifyapp.ui.viewModels.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class SettingsSection {
    HOME,
    LOCAL_DATABASE,
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FormScreen(
    settingsViewModel: SettingsViewModel,
) {
    var currentSection by rememberSaveable { androidx.compose.runtime.mutableStateOf(SettingsSection.HOME) }
    var accessDeniedMessage by rememberSaveable { androidx.compose.runtime.mutableStateOf<String?>(null) }
    var openTableTabs by rememberSaveable { androidx.compose.runtime.mutableStateOf(listOf<String>()) }
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val snapshot = uiState.snapshot
    val selectedTable = remember(snapshot, uiState.selectedTableName) {
        snapshot?.tables?.firstOrNull { it.name == uiState.selectedTableName }
    }
    val filteredRows = remember(selectedTable, uiState.rowSearchQuery, uiState.loadedRows) {
        val searchQuery = uiState.rowSearchQuery.trim()
        if (searchQuery.isBlank()) {
            uiState.loadedRows
        } else {
            uiState.loadedRows.filter {
                it.searchableText.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(currentSection, snapshot, uiState.selectedTableName) {
        val validTableNames = snapshot?.tables.orEmpty().map { it.name }.toSet()
        openTableTabs = openTableTabs.filter { it in validTableNames }

        val selectedName = uiState.selectedTableName
        if (
            currentSection == SettingsSection.LOCAL_DATABASE &&
            !selectedName.isNullOrBlank() &&
            selectedName in validTableNames &&
            selectedName !in openTableTabs
        ) {
            openTableTabs = (openTableTabs + selectedName).takeLast(8)
        }
    }

    fun openDatabaseTable(tableName: String) {
        openTableTabs = (openTableTabs.filterNot { it == tableName } + tableName).takeLast(8)
        settingsViewModel.selectTable(tableName)
    }

    fun closeDatabaseTab(tableName: String) {
        val remainingTabs = openTableTabs.filterNot { it == tableName }
        openTableTabs = remainingTabs

        if (uiState.selectedTableName == tableName) {
            val fallbackTableName = remainingTabs.lastOrNull()
                ?: snapshot?.tables
                    ?.firstOrNull { it.name != tableName }
                    ?.name

            if (!fallbackTableName.isNullOrBlank()) {
                openDatabaseTable(fallbackTableName)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (currentSection == SettingsSection.HOME) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SettingsHeroCard(uiState = uiState)

                accessDeniedMessage?.let { message ->
                    InlineNoticeCard(
                        message = message,
                        actionLabel = "Dismiss",
                        onAction = { accessDeniedMessage = null }
                    )
                }

                SettingsSectionHeader(
                    title = "Overview",
                    subtitle = "Version, account binding, and device metadata all live here now."
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    maxItemsInEachRow = 3
                ) {
                    AppOverviewCard(
                        uiState = uiState,
                        modifier = Modifier.widthIn(min = 280.dp, max = 420.dp)
                    )
                    AccountAccessCard(
                        uiState = uiState,
                        modifier = Modifier.widthIn(min = 280.dp, max = 420.dp)
                    )
                    DeviceDetailsCard(
                        uiState = uiState,
                        modifier = Modifier.widthIn(min = 280.dp, max = 420.dp)
                    )
                    DeviceIdentityCard(
                        uiState = uiState,
                        modifier = Modifier.widthIn(min = 280.dp, max = 420.dp)
                    )
                }

                SettingsSectionHeader(
                    title = "Tools",
                    subtitle = "Sensitive tools stay behind role-based access even though the Settings page is always visible."
                )

                SettingsCardsGrid(
                    canManageLocalDatabase = uiState.canManageLocalDatabase,
                    onOpenLocalDatabase = {
                        if (uiState.canManageLocalDatabase) {
                            accessDeniedMessage = null
                            settingsViewModel.ensureDatabaseSnapshotLoaded()
                            currentSection = SettingsSection.LOCAL_DATABASE
                        } else {
                            accessDeniedMessage =
                                "Access denied. Administrator permission is required to open Local Database Management."
                        }
                    }
                )
            }
        } else {
            TopbarWithButton(
                title = "Local Database Management",
                buttonLabel = "Back",
                buttonColor = AccentSecondary,
                onButtonPressed = {
                    currentSection = SettingsSection.HOME
                }
            )

            DebugSummaryRow(
                schemaVersion = snapshot?.schemaVersion,
                tableCount = snapshot?.tables?.size,
                totalRows = snapshot?.totalRows,
                loadedAtMillis = snapshot?.loadedAtMillis
            )

            when {
                uiState.isLoading && snapshot == null -> {
                    LoadingState()
                }

                uiState.errorMessage != null && snapshot == null -> {
                    ErrorState(
                        message = uiState.errorMessage.orEmpty(),
                        onRetry = settingsViewModel::refreshDatabaseSnapshot
                    )
                }

                else -> {
                    DatabaseWorkbenchPage(
                        modifier = Modifier.fillMaxSize(),
                        snapshot = snapshot,
                        selectedTable = selectedTable,
                        filteredRows = filteredRows,
                    isLoadingRows = uiState.isLoadingRows,
                    pageIndex = uiState.pageIndex,
                    pageCount = uiState.pageCount,
                    pageSize = uiState.pageSize,
                    rowSearchQuery = uiState.rowSearchQuery,
                    errorMessage = uiState.errorMessage,
                    openTableTabs = openTableTabs,
                    onOpenTable = ::openDatabaseTable,
                    onCloseTable = ::closeDatabaseTab,
                    onPreviousPage = settingsViewModel::goToPreviousPage,
                    onNextPage = settingsViewModel::goToNextPage,
                    onPageSizeChanged = settingsViewModel::updatePageSize,
                    onRowSearchQueryChanged = settingsViewModel::updateRowSearchQuery,
                    onRefreshRequested = settingsViewModel::refreshDatabaseSnapshot
                )
                }
            }
        }
    }
}

@Composable
private fun SettingsHeroCard(
    uiState: SettingsUiState,
) {
    val accessLabel = if (uiState.isAdmin) "Administrator" else "Standard Member"
    val accessColor = if (uiState.isAdmin) Accent else AccentSecondary
    val signedInAs = uiState.displayName.ifBlank {
        uiState.username.ifBlank { "Scoutify User" }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Accent.copy(alpha = 0.18f),
                        AccentSecondary.copy(alpha = 0.16f),
                        DarkGunmetal
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .border(1.dp, LightGunmetal, RoundedCornerShape(18.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Scoutify Settings",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Signed in as $signedInAs",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }

            StatusPill(
                text = accessLabel,
                containerColor = accessColor.copy(alpha = 0.14f),
                contentColor = accessColor
            )
        }

        Text(
            text = "The settings page is back for every user. Sensitive utilities such as local database inspection are now gated by admin privileges instead of disappearing entirely.",
            color = TextPrimary,
            fontSize = 14.sp,
            lineHeight = 21.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InfoPill(
                modifier = Modifier.weight(1f),
                label = "Version",
                value = uiState.appVersionName.ifBlank { "--" }
            )
            InfoPill(
                modifier = Modifier.weight(1f),
                label = "Build",
                value = uiState.buildTypeLabel.ifBlank { "--" }
            )
            InfoPill(
                modifier = Modifier.weight(1f),
                label = "Device Match",
                value = when (uiState.deviceIdMatches) {
                    true -> "Bound"
                    false -> "Mismatch"
                    null -> "Pending"
                }
            )
        }
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    subtitle: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun SettingsCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .background(DarkGunmetal, RoundedCornerShape(16.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(16.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
        HorizontalDivider(color = LightGunmetal)
        content()
    }
}

@Composable
private fun AppOverviewCard(
    uiState: SettingsUiState,
    modifier: Modifier = Modifier,
) {
    SettingsCard(
        modifier = modifier,
        title = "App Overview",
        subtitle = "Build metadata and package information for this installation."
    ) {
        MetadataRow(label = "Version Name", value = uiState.appVersionName)
        MetadataRow(label = "Version Code", value = uiState.appVersionCode)
        MetadataRow(label = "Build Type", value = uiState.buildTypeLabel)
        MetadataRow(label = "Package", value = uiState.packageName, monospace = true)
        MetadataRow(
            label = "Local Debugging",
            value = if (uiState.isDebugBuild) "Enabled for this build" else "Disabled in this build"
        )
    }
}

@Composable
private fun AccountAccessCard(
    uiState: SettingsUiState,
    modifier: Modifier = Modifier,
) {
    SettingsCard(
        modifier = modifier,
        title = "Account Access",
        subtitle = "Current login identity plus the role and permission claims used for access control."
    ) {
        MetadataRow(label = "Display Name", value = uiState.displayName)
        MetadataRow(label = "Username", value = uiState.username, monospace = true)
        MetadataRow(label = "Email", value = uiState.email, monospace = true)
        MetadataRow(label = "Access Level", value = if (uiState.isAdmin) "Administrator" else "Standard Member")
        MetadataRow(label = "Roles", value = uiState.roles.joinDisplayValue())
        MetadataRow(label = "Groups", value = uiState.groups.joinDisplayValue())
        MetadataRow(label = "Permissions", value = uiState.permissions.joinDisplayValue())
    }
}

@Composable
private fun DeviceDetailsCard(
    uiState: SettingsUiState,
    modifier: Modifier = Modifier,
) {
    SettingsCard(
        modifier = modifier,
        title = "Device Details",
        subtitle = "Hardware and OS details for the currently running handset or tablet."
    ) {
        MetadataRow(label = "Device", value = uiState.deviceDisplayName)
        MetadataRow(label = "Manufacturer", value = uiState.manufacturer)
        MetadataRow(label = "Brand", value = uiState.brand)
        MetadataRow(label = "Model", value = uiState.model)
        MetadataRow(label = "Android", value = "${uiState.androidVersion.ifBlank { "--" }} (SDK ${uiState.sdkInt})")
    }
}

@Composable
private fun DeviceIdentityCard(
    uiState: SettingsUiState,
    modifier: Modifier = Modifier,
) {
    SettingsCard(
        modifier = modifier,
        title = "Device Identity",
        subtitle = "Scoutify compares the local Android secure ID with the server-bound device ID stored for the account."
    ) {
        MetadataRow(label = "Local Device ID", value = uiState.localAndroidId, monospace = true)
        MetadataRow(label = "Registered Device ID", value = uiState.registeredAndroidId, monospace = true)
        MetadataRow(
            label = "Binding Status",
            value = when (uiState.deviceIdMatches) {
                true -> "Local and registered IDs match"
                false -> "IDs do not match"
                null -> "Registered device ID not available yet"
            }
        )
        Text(
            text = "This value comes from Android's secure settings (`Settings.Secure.ANDROID_ID`) and is then compared against the device ID returned by your authenticated user profile.",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun MetadataRow(
    label: String,
    value: String,
    monospace: Boolean = false,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            color = Deselected,
            fontSize = 12.sp
        )

        if (monospace) {
            SelectionContainer {
                Text(
                    text = value.ifBlank { "Not available" },
                    color = TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        } else {
            Text(
                text = value.ifBlank { "Not available" },
                color = TextPrimary,
                fontSize = 14.sp,
                lineHeight = 19.sp
            )
        }
    }
}

@Composable
private fun InlineNoticeCard(
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x33F87171), RoundedCornerShape(14.dp))
            .border(1.dp, Color(0x66F87171), RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message,
            color = Color(0xFFFECACA),
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = actionLabel,
            color = Accent,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onAction() }
        )
    }
}

@Composable
private fun StatusPill(
    text: String,
    containerColor: Color,
    contentColor: Color,
) {
    Box(
        modifier = Modifier
            .background(containerColor, RoundedCornerShape(999.dp))
            .border(1.dp, contentColor.copy(alpha = 0.35f), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun InfoPill(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
) {
    Column(
        modifier = modifier
            .background(Background.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            color = Deselected,
            fontSize = 11.sp
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SettingsCardsGrid(
    canManageLocalDatabase: Boolean,
    onOpenLocalDatabase: () -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        if (maxWidth >= 880.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SettingsFeatureCard(
                    modifier = Modifier.weight(1f),
                    title = "Local Database Management",
                    description = "Inspect local tables, rows, and fields on this device. This tool is now always listed here, but only administrators can open it.",
                    badge = if (canManageLocalDatabase) "Admin Ready" else "Restricted",
                    footer = if (canManageLocalDatabase) "Open management console" else "Tap to view access restriction",
                    accentColor = if (canManageLocalDatabase) AccentSecondary else Color(0xFFFCA5A5),
                    onClick = onOpenLocalDatabase
                )

                SettingsFeatureCard(
                    modifier = Modifier.weight(1f),
                    title = "Device Binding Health",
                    description = "Use the cards above to verify that this install is running on the same Android device ID that the backend has registered to the signed-in account.",
                    badge = "Info",
                    footer = "Identity details shown above",
                    accentColor = Accent
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SettingsFeatureCard(
                    title = "Local Database Management",
                    description = "Inspect local tables, rows, and fields on this device. This tool is now always listed here, but only administrators can open it.",
                    badge = if (canManageLocalDatabase) "Admin Ready" else "Restricted",
                    footer = if (canManageLocalDatabase) "Open management console" else "Tap to view access restriction",
                    accentColor = if (canManageLocalDatabase) AccentSecondary else Color(0xFFFCA5A5),
                    onClick = onOpenLocalDatabase
                )

                SettingsFeatureCard(
                    title = "Device Binding Health",
                    description = "Use the cards above to verify that this install is running on the same Android device ID that the backend has registered to the signed-in account.",
                    badge = "Info",
                    footer = "Identity details shown above",
                    accentColor = Accent
                )
            }
        }
    }
}

@Composable
private fun SettingsFeatureCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    badge: String,
    footer: String,
    accentColor: Color,
    onClick: (() -> Unit)? = null,
) {
    val cardModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    Column(
        modifier = cardModifier
            .height(220.dp)
            .background(DarkGunmetal, RoundedCornerShape(16.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(16.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            StatusPill(
                text = badge,
                containerColor = accentColor.copy(alpha = 0.14f),
                contentColor = accentColor
            )
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }

        Text(
            text = footer,
            color = accentColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun DebugSummaryRow(
    schemaVersion: Long?,
    tableCount: Int?,
    totalRows: Int?,
    loadedAtMillis: Long?,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        if (maxWidth >= 720.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Schema",
                    value = schemaVersion?.toString() ?: "--",
                    subtitle = "SQLDelight version"
                )
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Tables",
                    value = tableCount?.toString() ?: "--",
                    subtitle = "Loaded from sqlite_master"
                )
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Rows",
                    value = totalRows?.toString() ?: "--",
                    subtitle = loadedAtMillis?.let { "Refreshed ${formatTimestamp(it)}" } ?: "No snapshot yet"
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    title = "Schema",
                    value = schemaVersion?.toString() ?: "--",
                    subtitle = "SQLDelight version"
                )
                SummaryCard(
                    title = "Tables",
                    value = tableCount?.toString() ?: "--",
                    subtitle = "Loaded from sqlite_master"
                )
                SummaryCard(
                    title = "Rows",
                    value = totalRows?.toString() ?: "--",
                    subtitle = loadedAtMillis?.let { "Refreshed ${formatTimestamp(it)}" } ?: "No snapshot yet"
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
) {
    Column(
        modifier = modifier
            .background(DarkGunmetal, RoundedCornerShape(12.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = title, color = Deselected, fontSize = 12.sp)
        Text(text = value, color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = subtitle, color = TextSecondary, fontSize = 12.sp)
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(color = Accent)
            Text("Loading local database snapshot...", color = TextSecondary)
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGunmetal, RoundedCornerShape(12.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(12.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Database Access Failed",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = message, color = TextSecondary, fontSize = 14.sp)
        Text(
            text = "Tap to retry",
            color = Accent,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onRetry() }
        )
    }
}

@Composable
private fun DatabaseTablesPane(
    modifier: Modifier = Modifier,
    tables: List<LocalDatabaseDebugTable>,
    selectedTableName: String?,
    onTableSelected: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(DarkGunmetal, RoundedCornerShape(12.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Local Tables",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Browse every non-system table currently stored on the device.",
            color = TextSecondary,
            fontSize = 13.sp
        )
        HorizontalDivider(color = LightGunmetal)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tables, key = { it.name }) { table ->
                DatabaseTableListItem(
                    table = table,
                    selected = table.name == selectedTableName,
                    onClick = { onTableSelected(table.name) }
                )
            }
        }
    }
}

@Composable
private fun DatabaseTableListItem(
    table: LocalDatabaseDebugTable,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor = if (selected) Accent.copy(alpha = 0.18f) else DarkishGunmetal
    val borderColor = if (selected) Accent else LightGunmetal

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(10.dp))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = table.name,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Text(
                text = "${table.columns.size} columns",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        Box(
            modifier = Modifier
                .background(Background, RoundedCornerShape(999.dp))
                .border(1.dp, borderColor, RoundedCornerShape(999.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = table.rowCount.toString(),
                color = if (selected) Accent else Deselected,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DatabaseTableDetailPane(
    modifier: Modifier = Modifier,
    table: LocalDatabaseDebugTable?,
    filteredRows: List<LocalDatabaseDebugRow>,
    isLoadingRows: Boolean,
    rowSearchQuery: String,
    onRowSearchQueryChanged: (String) -> Unit,
    errorMessage: String?,
    onRefreshRequested: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(DarkGunmetal, RoundedCornerShape(12.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (table == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No table selected.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
            return
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = table.name,
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${table.rowCount} rows in local storage",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            Box(
                modifier = Modifier
                    .background(Accent.copy(alpha = 0.12f), RoundedCornerShape(999.dp))
                    .border(1.dp, Accent.copy(alpha = 0.45f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${table.columns.size} fields",
                    color = Accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SearchField(
                modifier = Modifier.weight(1f),
                value = rowSearchQuery,
                placeholder = "Search row values or field names",
                onValueChange = onRowSearchQueryChanged
            )

            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .background(Color(0x33F87171), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0x66F87171), RoundedCornerShape(10.dp))
                        .clickable { onRefreshRequested() }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Retry refresh",
                        color = Color(0xFFFCA5A5),
                        fontSize = 12.sp
                    )
                }
            }
        }

        TableColumnsCard(table = table)

        if (isLoadingRows) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Accent)
            }
        } else if (filteredRows.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (table.rowCount == 0) {
                        "This table is currently empty."
                    } else {
                        "No rows match the current search."
                    },
                    color = TextSecondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredRows, key = { "${table.name}-${it.index}" }) { row ->
                    TableRowCard(row = row)
                }
            }
        }
    }
}

@Composable
private fun SearchField(
    modifier: Modifier = Modifier,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        cursorBrush = SolidColor(TextPrimary),
        singleLine = true,
        textStyle = TextStyle(
            color = TextPrimary,
            fontSize = 14.sp
        ),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                if (value.isBlank()) {
                    Text(
                        text = placeholder,
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
                innerTextField()
            }
        },
        modifier = modifier
            .background(TextFieldBackground, RoundedCornerShape(10.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(10.dp))
    )
}

@Composable
private fun TableColumnsCard(
    table: LocalDatabaseDebugTable,
) {
    val scrollState = rememberScrollState()
    val columnText = remember(table) {
        table.columns.joinToString(separator = "\n") { column ->
            buildString {
                append(column.name)
                append(" : ")
                append(if (column.type.isBlank()) "UNKNOWN" else column.type)
                if (column.primaryKeyPosition > 0) {
                    append("  PK(")
                    append(column.primaryKeyPosition)
                    append(")")
                }
                if (column.notNull) {
                    append("  NOT NULL")
                }
                if (!column.defaultValue.isNullOrBlank()) {
                    append("  DEFAULT ")
                    append(column.defaultValue)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 220.dp)
            .background(DarkishGunmetal, RoundedCornerShape(10.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Columns",
            color = Deselected,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        SelectionContainer {
            Text(
                text = columnText,
                color = TextPrimary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 18.sp,
                modifier = Modifier.verticalScroll(scrollState)
            )
        }
    }
}

@Composable
private fun TableRowCard(
    row: LocalDatabaseDebugRow,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkishGunmetal, RoundedCornerShape(10.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(10.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Row ${row.index + 1}",
            color = Accent,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        SelectionContainer {
            Text(
                text = row.searchableText,
                color = TextPrimary,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

private fun List<String>.joinDisplayValue(): String {
    return if (isEmpty()) "None detected" else joinToString(", ")
}

private fun formatTimestamp(timestamp: Long): String {
    return SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(timestamp))
}
