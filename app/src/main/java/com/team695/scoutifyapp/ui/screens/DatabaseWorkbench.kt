package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team695.scoutifyapp.data.repository.LocalDatabaseDebugRow
import com.team695.scoutifyapp.data.repository.LocalDatabaseDebugSnapshot
import com.team695.scoutifyapp.data.repository.LocalDatabaseDebugTable
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

private enum class ColumnWidthMode {
    AUTO,
    COMPACT,
    WIDE,
    CUSTOM,
}

@Composable
fun DatabaseWorkbenchPage(
    modifier: Modifier = Modifier,
    snapshot: LocalDatabaseDebugSnapshot?,
    selectedTable: LocalDatabaseDebugTable?,
    filteredRows: List<LocalDatabaseDebugRow>,
    isLoadingRows: Boolean,
    pageIndex: Int,
    pageCount: Int,
    pageSize: Int,
    rowSearchQuery: String,
    errorMessage: String?,
    openTableTabs: List<String>,
    onOpenTable: (String) -> Unit,
    onCloseTable: (String) -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    onPageSizeChanged: (Int) -> Unit,
    onRowSearchQueryChanged: (String) -> Unit,
    onRefreshRequested: () -> Unit,
) {
    var widthMode by rememberSaveable { mutableStateOf(ColumnWidthMode.AUTO.name) }
    val customColumnWidths = remember { mutableStateMapOf<String, Float>() }

    BoxWithConstraints(modifier = modifier) {
        val useSidebarLayout = maxWidth >= 760.dp
        val sidebarWidth = when {
            maxWidth >= 1200.dp -> 228.dp
            maxWidth >= 940.dp -> 196.dp
            else -> 164.dp
        }

        if (useSidebarLayout) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DatabaseNavigatorPaneCompact(
                    modifier = Modifier
                        .width(sidebarWidth)
                        .fillMaxHeight(),
                    snapshot = snapshot,
                    selectedTableName = selectedTable?.name,
                    onOpenTable = onOpenTable
                )

                DatabaseWorkspacePane(
                    modifier = Modifier.weight(1f),
                    selectedTable = selectedTable,
                    filteredRows = filteredRows,
                    isLoadingRows = isLoadingRows,
                    pageIndex = pageIndex,
                    pageCount = pageCount,
                    pageSize = pageSize,
                    rowSearchQuery = rowSearchQuery,
                    errorMessage = errorMessage,
                    openTableTabs = openTableTabs,
                    widthMode = ColumnWidthMode.valueOf(widthMode),
                    customColumnWidths = customColumnWidths,
                    onOpenTable = onOpenTable,
                    onCloseTable = onCloseTable,
                    onPreviousPage = onPreviousPage,
                    onNextPage = onNextPage,
                    onPageSizeChanged = onPageSizeChanged,
                    onWidthModeChanged = { widthMode = it.name },
                    onRowSearchQueryChanged = onRowSearchQueryChanged,
                    onRefreshRequested = onRefreshRequested
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DatabaseNavigatorPaneCompact(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 132.dp, max = 188.dp),
                    snapshot = snapshot,
                    selectedTableName = selectedTable?.name,
                    onOpenTable = onOpenTable
                )

                DatabaseWorkspacePane(
                    modifier = Modifier.fillMaxSize(),
                    selectedTable = selectedTable,
                    filteredRows = filteredRows,
                    isLoadingRows = isLoadingRows,
                    pageIndex = pageIndex,
                    pageCount = pageCount,
                    pageSize = pageSize,
                    rowSearchQuery = rowSearchQuery,
                    errorMessage = errorMessage,
                    openTableTabs = openTableTabs,
                    widthMode = ColumnWidthMode.valueOf(widthMode),
                    customColumnWidths = customColumnWidths,
                    onOpenTable = onOpenTable,
                    onCloseTable = onCloseTable,
                    onPreviousPage = onPreviousPage,
                    onNextPage = onNextPage,
                    onPageSizeChanged = onPageSizeChanged,
                    onWidthModeChanged = { widthMode = it.name },
                    onRowSearchQueryChanged = onRowSearchQueryChanged,
                    onRefreshRequested = onRefreshRequested
                )
            }
        }
    }
}

@Composable
private fun DatabaseNavigatorPaneCompact(
    modifier: Modifier,
    snapshot: LocalDatabaseDebugSnapshot?,
    selectedTableName: String?,
    onOpenTable: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .background(DarkGunmetal, RoundedCornerShape(12.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Navigator",
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "${snapshot?.tables?.size ?: 0} tables",
            color = TextSecondary,
            fontSize = 10.sp
        )

        HorizontalDivider(color = LightGunmetal)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(snapshot?.tables.orEmpty(), key = { it.name }) { table ->
                CompactNavigatorItem(
                    table = table,
                    selected = table.name == selectedTableName,
                    onClick = { onOpenTable(table.name) }
                )
            }
        }
    }
}

@Composable
private fun CompactNavigatorItem(
    table: LocalDatabaseDebugTable,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val containerColor = if (selected) Accent.copy(alpha = 0.16f) else DarkishGunmetal
    val borderColor = if (selected) Accent else LightGunmetal

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = table.name,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${table.columns.size} cols",
                color = TextSecondary,
                fontSize = 9.sp
            )
        }

        Text(
            text = table.rowCount.toString(),
            color = if (selected) Accent else Deselected,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DatabaseWorkspacePane(
    modifier: Modifier,
    selectedTable: LocalDatabaseDebugTable?,
    filteredRows: List<LocalDatabaseDebugRow>,
    isLoadingRows: Boolean,
    pageIndex: Int,
    pageCount: Int,
    pageSize: Int,
    rowSearchQuery: String,
    errorMessage: String?,
    openTableTabs: List<String>,
    widthMode: ColumnWidthMode,
    customColumnWidths: MutableMap<String, Float>,
    onOpenTable: (String) -> Unit,
    onCloseTable: (String) -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    onPageSizeChanged: (Int) -> Unit,
    onWidthModeChanged: (ColumnWidthMode) -> Unit,
    onRowSearchQueryChanged: (String) -> Unit,
    onRefreshRequested: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(DarkGunmetal, RoundedCornerShape(12.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DatabaseTabStrip(
            openTableTabs = openTableTabs,
            selectedTableName = selectedTable?.name,
            onOpenTable = onOpenTable,
            onCloseTable = onCloseTable
        )

        DatabaseToolbarStrip(
            table = selectedTable,
            rowSearchQuery = rowSearchQuery,
            errorMessage = errorMessage,
            widthMode = widthMode,
            pageSize = pageSize,
            onWidthModeChanged = onWidthModeChanged,
            onPageSizeChanged = onPageSizeChanged,
            onRowSearchQueryChanged = onRowSearchQueryChanged,
            onRefreshRequested = onRefreshRequested
        )

        if (selectedTable == null) {
            CompactEmptyState(
                message = "Open a table from the navigator to preview its rows."
            )
            return
        }

        DatabaseMiniStatusStrip(table = selectedTable, filteredRows = filteredRows)

        if (isLoadingRows) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Accent,
                    strokeWidth = 2.dp
                )
            }
        } else if (filteredRows.isEmpty()) {
            CompactEmptyState(
                message = if (selectedTable.rowCount == 0) {
                    "This table has no rows."
                } else {
                    "No rows match the current filter."
                }
            )
        } else {
            DatabaseSpreadsheetGrid(
                modifier = Modifier.weight(1f),
                table = selectedTable,
                rows = filteredRows,
                widthMode = widthMode,
                customColumnWidths = customColumnWidths,
                onColumnWidthChanged = { columnName, width ->
                    customColumnWidths[columnName] = width.value
                    onWidthModeChanged(ColumnWidthMode.CUSTOM)
                }
            )
        }

        DatabasePaginationStrip(
            pageIndex = pageIndex,
            pageCount = pageCount,
            pageSize = pageSize,
            onPreviousPage = onPreviousPage,
            onNextPage = onNextPage,
            onPageSizeChanged = onPageSizeChanged
        )
    }
}

@Composable
private fun DatabaseTabStrip(
    openTableTabs: List<String>,
    selectedTableName: String?,
    onOpenTable: (String) -> Unit,
    onCloseTable: (String) -> Unit,
) {
    if (openTableTabs.isEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .background(Background, RoundedCornerShape(8.dp))
                .border(1.dp, LightGunmetal, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "No open tables",
                color = TextSecondary,
                fontSize = 10.sp
            )
        }
        return
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(openTableTabs, key = { it }) { tableName ->
            val selected = tableName == selectedTableName
            val containerColor = if (selected) Accent.copy(alpha = 0.18f) else Background
            val borderColor = if (selected) Accent else LightGunmetal

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(containerColor)
                    .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                    .clickable { onOpenTable(tableName) }
                    .padding(start = 10.dp, end = 8.dp, top = 7.dp, bottom = 7.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tableName,
                    color = if (selected) Accent else TextPrimary,
                    fontSize = 10.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                )
                Text(
                    text = "x",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    modifier = Modifier.clickable { onCloseTable(tableName) }
                )
            }
        }
    }
}

@Composable
private fun DatabaseToolbarStrip(
    table: LocalDatabaseDebugTable?,
    rowSearchQuery: String,
    errorMessage: String?,
    widthMode: ColumnWidthMode,
    pageSize: Int,
    onWidthModeChanged: (ColumnWidthMode) -> Unit,
    onPageSizeChanged: (Int) -> Unit,
    onRowSearchQueryChanged: (String) -> Unit,
    onRefreshRequested: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WorkbenchSearchField(
            modifier = Modifier.weight(1f),
            value = rowSearchQuery,
            placeholder = "Filter rows or field values",
            onValueChange = onRowSearchQueryChanged
        )

        CompactActionChip(
            label = table?.name ?: "No table",
            highlighted = table != null,
            onClick = {}
        )

        CompactActionChip(
            label = "Width ${widthMode.name.lowercase().replaceFirstChar(Char::uppercase)}",
            highlighted = widthMode != ColumnWidthMode.AUTO,
            onClick = {
                onWidthModeChanged(
                    when (widthMode) {
                        ColumnWidthMode.AUTO -> ColumnWidthMode.COMPACT
                        ColumnWidthMode.COMPACT -> ColumnWidthMode.WIDE
                        ColumnWidthMode.WIDE -> ColumnWidthMode.AUTO
                        ColumnWidthMode.CUSTOM -> ColumnWidthMode.AUTO
                    }
                )
            }
        )

        CompactActionChip(
            label = "Page $pageSize",
            highlighted = true,
            onClick = {
                onPageSizeChanged(
                    when (pageSize) {
                        50 -> 100
                        100 -> 250
                        250 -> 50
                        else -> 100
                    }
                )
            }
        )

        CompactActionChip(
            label = if (errorMessage.isNullOrBlank()) "Refresh" else "Retry",
            highlighted = !errorMessage.isNullOrBlank(),
            onClick = onRefreshRequested
        )
    }
}

@Composable
private fun DatabasePaginationStrip(
    pageIndex: Int,
    pageCount: Int,
    pageSize: Int,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    onPageSizeChanged: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Background, RoundedCornerShape(8.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            CompactActionChip(
                label = "Prev",
                highlighted = pageIndex > 0,
                onClick = onPreviousPage
            )
            CompactActionChip(
                label = "Next",
                highlighted = pageIndex + 1 < pageCount,
                onClick = onNextPage
            )
        }

        Text(
            text = "Page ${pageIndex + 1} / $pageCount",
            color = TextPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )

        CompactActionChip(
            label = "${pageSize} rows/page",
            highlighted = true,
            onClick = {
                onPageSizeChanged(
                    when (pageSize) {
                        50 -> 100
                        100 -> 250
                        250 -> 50
                        else -> 100
                    }
                )
            }
        )
    }
}

@Composable
private fun WorkbenchSearchField(
    modifier: Modifier = Modifier,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        cursorBrush = SolidColor(TextPrimary),
        textStyle = TextStyle(
            color = TextPrimary,
            fontSize = 11.sp
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isBlank()) {
                    Text(
                        text = placeholder,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
                innerTextField()
            }
        },
        modifier = modifier
            .height(34.dp)
            .background(TextFieldBackground, RoundedCornerShape(8.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(8.dp))
    )
}

@Composable
private fun CompactActionChip(
    label: String,
    highlighted: Boolean,
    onClick: () -> Unit,
) {
    val contentColor = if (highlighted) AccentSecondary else Deselected
    val borderColor = if (highlighted) AccentSecondary.copy(alpha = 0.45f) else LightGunmetal

    Box(
        modifier = Modifier
            .height(34.dp)
            .background(Background, RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = contentColor,
            fontSize = 10.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun DatabaseMiniStatusStrip(
    table: LocalDatabaseDebugTable,
    filteredRows: List<LocalDatabaseDebugRow>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Background, RoundedCornerShape(8.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusLabel(label = "Table", value = table.name)
        StatusLabel(label = "Rows", value = "${filteredRows.size}/${table.rowCount}")
        StatusLabel(label = "Columns", value = table.columns.size.toString())
    }
}

@Composable
private fun StatusLabel(
    label: String,
    value: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 9.sp
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DatabaseSpreadsheetGrid(
    modifier: Modifier,
    table: LocalDatabaseDebugTable,
    rows: List<LocalDatabaseDebugRow>,
    widthMode: ColumnWidthMode,
    customColumnWidths: Map<String, Float>,
    onColumnWidthChanged: (String, Dp) -> Unit,
) {
    val horizontalScrollState = rememberScrollState()
    val rowIndexWidth = 46.dp
    val density = LocalDensity.current
    val columnWidths = remember(table, widthMode, customColumnWidths) {
        table.columns.associate { column ->
            val adaptiveWidth = when {
                column.name.length >= 20 -> 168.dp
                column.name.length >= 12 -> 136.dp
                else -> 112.dp
            }
            val modeWidth = when (widthMode) {
                ColumnWidthMode.AUTO -> adaptiveWidth
                ColumnWidthMode.COMPACT -> 96.dp
                ColumnWidthMode.WIDE -> 160.dp
                ColumnWidthMode.CUSTOM -> customColumnWidths[column.name]?.dp ?: adaptiveWidth
            }
            column.name to (customColumnWidths[column.name]?.dp ?: modeWidth)
        }
    }
    val totalWidth = rowIndexWidth + table.columns.fold(0.dp) { acc, column ->
        acc + (columnWidths[column.name] ?: 112.dp)
    }

    Box(
        modifier = modifier
            .background(Background, RoundedCornerShape(10.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScrollState)
            ) {
                SpreadsheetHeaderRow(
                    table = table,
                    rowIndexWidth = rowIndexWidth,
                    columnWidths = columnWidths,
                    onColumnWidthChanged = { columnName, delta ->
                        val currentWidth = columnWidths[columnName] ?: 112.dp
                        val nextWidth = (currentWidth + with(density) { delta.toDp() })
                            .coerceIn(72.dp, 280.dp)
                        onColumnWidthChanged(columnName, nextWidth)
                    }
                )
            }

            HorizontalDivider(color = LightGunmetal)

            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(horizontalScrollState)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .width(totalWidth)
                        .fillMaxHeight()
                ) {
                    items(rows, key = { it.index }) { row ->
                        SpreadsheetDataRow(
                            table = table,
                            row = row,
                            rowIndexWidth = rowIndexWidth,
                            columnWidths = columnWidths
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpreadsheetHeaderRow(
    table: LocalDatabaseDebugTable,
    rowIndexWidth: Dp,
    columnWidths: Map<String, Dp>,
    onColumnWidthChanged: (String, Float) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkGunmetal)
    ) {
        SpreadsheetHeaderCell(
            modifier = Modifier.width(rowIndexWidth),
            title = "#",
            subtitle = ""
        )

        table.columns.forEach { column ->
            SpreadsheetHeaderCell(
                modifier = Modifier.width(columnWidths[column.name] ?: 112.dp),
                title = column.name,
                subtitle = if (column.type.isBlank()) "TEXT" else column.type,
                onWidthDragged = { dragAmount -> onColumnWidthChanged(column.name, dragAmount) }
            )
        }
    }
}

@Composable
private fun SpreadsheetHeaderCell(
    modifier: Modifier,
    title: String,
    subtitle: String,
    onWidthDragged: ((Float) -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .height(42.dp)
            .border(0.5.dp, LightGunmetal)
            .padding(start = 6.dp, top = 4.dp, bottom = 4.dp, end = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    color = Deselected,
                    fontSize = 8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Box(
            modifier = Modifier
                .width(8.dp)
                .fillMaxHeight()
                .pointerInput(onWidthDragged) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        onWidthDragged?.invoke(dragAmount.x)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(22.dp)
                    .background(AccentSecondary.copy(alpha = 0.7f), RoundedCornerShape(999.dp))
            )
        }
    }
}

@Composable
private fun SpreadsheetDataRow(
    table: LocalDatabaseDebugTable,
    row: LocalDatabaseDebugRow,
    rowIndexWidth: Dp,
    columnWidths: Map<String, Dp>,
) {
    val rowColor = if (row.index % 2 == 0) Background else DarkishGunmetal.copy(alpha = 0.42f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowColor)
    ) {
        SpreadsheetValueCell(
            modifier = Modifier.width(rowIndexWidth),
            value = (row.index + 1).toString(),
            emphasize = true
        )

        table.columns.forEachIndexed { columnIndex, _ ->
            SpreadsheetValueCell(
                modifier = Modifier.width(columnWidths[table.columns[columnIndex].name] ?: 112.dp),
                value = row.cells.getOrNull(columnIndex)?.value.orEmpty()
            )
        }
    }
}

@Composable
private fun SpreadsheetValueCell(
    modifier: Modifier,
    value: String,
    emphasize: Boolean = false,
) {
    Box(
        modifier = modifier
            .height(34.dp)
            .border(0.5.dp, LightGunmetal)
            .padding(horizontal = 6.dp, vertical = 5.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = value.ifBlank { "NULL" },
            color = if (emphasize) Accent else TextPrimary,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (emphasize) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CompactEmptyState(
    message: String,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background, RoundedCornerShape(10.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = TextSecondary,
            fontSize = 11.sp
        )
    }
}
