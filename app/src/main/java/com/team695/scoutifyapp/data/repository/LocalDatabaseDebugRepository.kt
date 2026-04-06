package com.team695.scoutifyapp.data.repository

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement
import com.team695.scoutifyapp.config.DebugConfig
import com.team695.scoutifyapp.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class LocalDatabaseDebugSnapshot(
    val schemaVersion: Long,
    val loadedAtMillis: Long,
    val tables: List<LocalDatabaseDebugTable>,
) {
    val totalRows: Int
        get() = tables.sumOf { it.rowCount }
}

data class LocalDatabaseDebugTable(
    val name: String,
    val rowCount: Int,
    val columns: List<LocalDatabaseDebugColumn>,
    val rows: List<LocalDatabaseDebugRow> = emptyList(),
)

data class LocalDatabaseDebugColumn(
    val name: String,
    val type: String,
    val notNull: Boolean,
    val defaultValue: String?,
    val primaryKeyPosition: Int,
)

data class LocalDatabaseDebugRow(
    val index: Int,
    val cells: List<LocalDatabaseDebugCell>,
) {
    val searchableText: String
        get() = cells.joinToString(separator = "\n") { "${it.column}: ${it.value}" }
}

data class LocalDatabaseDebugCell(
    val column: String,
    val value: String,
)

class LocalDatabaseDebugRepository(
    private val driver: SqlDriver,
) {
    private val redactedColumnPattern = Regex("token|secret|password|api[_-]?key|email", RegexOption.IGNORE_CASE)

    suspend fun loadSnapshot(): LocalDatabaseDebugSnapshot {
        return withContext(Dispatchers.IO) {
            requireDebugAccess()
            val tableNames = loadTableNames()
            val tables = tableNames.map { tableName ->
                LocalDatabaseDebugTable(
                    name = tableName,
                    rowCount = getTableRowCount(tableName),
                    columns = loadColumns(tableName)
                )
            }

            LocalDatabaseDebugSnapshot(
                schemaVersion = AppDatabase.Schema.version,
                loadedAtMillis = System.currentTimeMillis(),
                tables = tables
            )
        }
    }

    suspend fun loadRowsForTable(
        tableName: String,
        limit: Int = DEFAULT_ROW_PAGE_SIZE,
        offset: Int = 0,
    ): List<LocalDatabaseDebugRow> {
        return withContext(Dispatchers.IO) {
            requireDebugAccess()
            val normalizedLimit = limit.coerceIn(1, MAX_ROW_PAGE_SIZE)
            val normalizedOffset = offset.coerceAtLeast(0)
            val columns = loadColumns(tableName)

            loadRows(
                tableName = tableName,
                columns = columns,
                limit = normalizedLimit,
                offset = normalizedOffset
            )
        }
    }

    private fun loadTableNames(): List<String> {
        return executeQuery(
            sql = """
                SELECT name
                FROM sqlite_master
                WHERE type = 'table'
                AND name NOT LIKE 'sqlite_%'
                ORDER BY name ASC
            """.trimIndent()
        ) { cursor ->
            val tables = mutableListOf<String>()
            while (cursor.next().value) {
                tables += cursor.getString(0).orEmpty()
            }
            tables
        }
    }

    private fun getTableRowCount(tableName: String): Int {
        val escapedTableName = tableName.replace("\"", "\"\"")

        return executeQuery(
            sql = "SELECT COUNT(*) FROM \"$escapedTableName\""
        ) { cursor ->
            if (cursor.next().value) {
                cursor.getLong(0)?.toInt() ?: 0
            } else {
                0
            }
        }
    }

    private fun loadColumns(tableName: String): List<LocalDatabaseDebugColumn> {
        val escapedTableName = tableName.replace("\"", "\"\"")

        return executeQuery(
            sql = "PRAGMA table_info(\"$escapedTableName\")"
        ) { cursor ->
            val columns = mutableListOf<LocalDatabaseDebugColumn>()
            while (cursor.next().value) {
                columns += LocalDatabaseDebugColumn(
                    name = cursor.getString(1).orEmpty(),
                    type = cursor.getString(2).orEmpty(),
                    notNull = cursor.getLong(3) == 1L,
                    defaultValue = cursor.getString(4),
                    primaryKeyPosition = cursor.getLong(5)?.toInt() ?: 0
                )
            }
            columns
        }
    }

    private fun loadRows(
        tableName: String,
        columns: List<LocalDatabaseDebugColumn>,
        limit: Int,
        offset: Int,
    ): List<LocalDatabaseDebugRow> {
        val escapedTableName = tableName.replace("\"", "\"\"")

        return executeQuery(
            sql = "SELECT * FROM \"$escapedTableName\" LIMIT $limit OFFSET $offset"
        ) { cursor ->
            val rows = mutableListOf<LocalDatabaseDebugRow>()
            var index = offset

            while (cursor.next().value) {
                rows += LocalDatabaseDebugRow(
                    index = index,
                    cells = columns.mapIndexed { columnIndex, column ->
                        LocalDatabaseDebugCell(
                            column = column.name,
                            value = readValue(cursor, columnIndex, column)
                        )
                    }
                )
                index += 1
            }

            rows
        }
    }

    private fun readValue(
        cursor: SqlCursor,
        columnIndex: Int,
        column: LocalDatabaseDebugColumn,
    ): String {
        if (redactedColumnPattern.containsMatchIn(column.name)) {
            return "<REDACTED>"
        }

        val normalizedType = column.type.uppercase()

        return when {
            normalizedType.contains("INT") ->
                cursor.getLong(columnIndex)?.toString()
                    ?: cursor.getString(columnIndex)
                    ?: cursor.getDouble(columnIndex)?.toString()

            normalizedType.contains("REAL")
                || normalizedType.contains("FLOA")
                || normalizedType.contains("DOUB") ->
                cursor.getDouble(columnIndex)?.toString()
                    ?: cursor.getLong(columnIndex)?.toString()
                    ?: cursor.getString(columnIndex)

            normalizedType.contains("BLOB") ->
                cursor.getBytes(columnIndex)?.let { "<${it.size} bytes>" }

            else ->
                cursor.getString(columnIndex)
                    ?: cursor.getLong(columnIndex)?.toString()
                    ?: cursor.getDouble(columnIndex)?.toString()
                    ?: cursor.getBytes(columnIndex)?.let { "<${it.size} bytes>" }
        } ?: "NULL"
    }

    private fun requireDebugAccess() {
        check(DebugConfig.ENABLE_LOCAL_DATABASE_DEBUGGING) {
            "Local database debugging is disabled in this build."
        }
    }

    private fun <T> executeQuery(
        sql: String,
        parameterCount: Int = 0,
        binders: SqlPreparedStatement.() -> Unit = {},
        mapper: (SqlCursor) -> T,
    ): T {
        return driver.executeQuery(
            identifier = null,
            sql = sql,
            mapper = { cursor -> QueryResult.Value(mapper(cursor)) },
            parameters = parameterCount,
            binders = binders
        ).value
    }

    private companion object {
        const val DEFAULT_ROW_PAGE_SIZE = 250
        const val MAX_ROW_PAGE_SIZE = 500
    }
}
