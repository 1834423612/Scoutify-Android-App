package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.text.selection.SelectionContainer
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
import com.team695.scoutifyapp.ui.viewModels.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class SettingsSection {
    HOME,
    LOCAL_DATABASE,
}

@Composable
fun FormScreen(
    settingsViewModel: SettingsViewModel,
) {
    var currentSection by rememberSaveable { androidx.compose.runtime.mutableStateOf(SettingsSection.HOME) }
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val snapshot = uiState.snapshot
    val selectedTable = remember(snapshot, uiState.selectedTableName) {
        snapshot?.tables?.firstOrNull { it.name == uiState.selectedTableName }
    }
    val filteredRows = remember(selectedTable, uiState.rowSearchQuery) {
        val searchQuery = uiState.rowSearchQuery.trim()
        if (searchQuery.isBlank()) {
            uiState.loadedRows
        } else {
            uiState.loadedRows.filter {
                it.searchableText.contains(searchQuery, ignoreCase = true)
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
            SettingsHomeHeader()
            SettingsCardsGrid(
                onOpenLocalDatabase = {
                    currentSection = SettingsSection.LOCAL_DATABASE
                }
            )
        } else {
            TopbarWithButton(
                title = "Local Database",
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
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val useTwoPaneLayout = maxWidth >= 950.dp

                        if (useTwoPaneLayout) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                DatabaseTablesPane(
                                    modifier = Modifier.weight(0.32f),
                                    tables = snapshot?.tables.orEmpty(),
                                    selectedTableName = selectedTable?.name,
                                    onTableSelected = settingsViewModel::selectTable
                                )

                                DatabaseTableDetailPane(
                                    modifier = Modifier.weight(0.68f),
                                    table = selectedTable,
                                    filteredRows = filteredRows,
                                    isLoadingRows = uiState.isLoadingRows,
                                    rowSearchQuery = uiState.rowSearchQuery,
                                    onRowSearchQueryChanged = settingsViewModel::updateRowSearchQuery,
                                    errorMessage = uiState.errorMessage,
                                    onRefreshRequested = settingsViewModel::refreshDatabaseSnapshot
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                DatabaseTablesPane(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(0.4f),
                                    tables = snapshot?.tables.orEmpty(),
                                    selectedTableName = selectedTable?.name,
                                    onTableSelected = settingsViewModel::selectTable
                                )

                                DatabaseTableDetailPane(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(0.6f),
                                    table = selectedTable,
                                    filteredRows = filteredRows,
                                    isLoadingRows = uiState.isLoadingRows,
                                    rowSearchQuery = uiState.rowSearchQuery,
                                    onRowSearchQueryChanged = settingsViewModel::updateRowSearchQuery,
                                    errorMessage = uiState.errorMessage,
                                    onRefreshRequested = settingsViewModel::refreshDatabaseSnapshot
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
private fun SettingsHomeHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Settings",
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Choose a tool below. Database inspection lives here as a debug utility, but this page can keep growing with more settings later.",
            color = TextSecondary,
            fontSize = 14.sp
        )
        HorizontalDivider(color = LightGunmetal)
    }
}

@Composable
private fun SettingsCardsGrid(
    onOpenLocalDatabase: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SettingsActionCard(
            modifier = Modifier.weight(1f),
            title = "Local Database Preview",
            description = "Inspect every local table, row, and field currently stored on the device for offline-sync debugging.",
            badge = "Debug",
            onClick = onOpenLocalDatabase
        )

        SettingsActionCard(
            modifier = Modifier.weight(1f),
            title = "More Settings Soon",
            description = "This slot is intentionally left here so later members can add upload diagnostics, cache tools, or app preferences without reworking the page.",
            badge = "Reserved",
            enabled = false,
            onClick = {}
        )
    }
}

@Composable
private fun SettingsActionCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    badge: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val backgroundColor = if (enabled) DarkGunmetal else DarkishGunmetal
    val borderColor = if (enabled) LightGunmetal else LightGunmetal.copy(alpha = 0.5f)
    val badgeColor = if (enabled) Accent else Deselected

    Column(
        modifier = modifier
            .height(220.dp)
            .background(backgroundColor, RoundedCornerShape(14.dp))
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(18.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .background(Background, RoundedCornerShape(999.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badge,
                    color = badgeColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
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
            text = if (enabled) "Open" else "Coming later",
            color = if (enabled) AccentSecondary else Deselected,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
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
            text = "Database Debug Failed",
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

private fun formatTimestamp(timestamp: Long): String {
    return SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(timestamp))
}
