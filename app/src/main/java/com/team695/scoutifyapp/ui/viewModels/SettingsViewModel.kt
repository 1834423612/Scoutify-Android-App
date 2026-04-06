package com.team695.scoutifyapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team695.scoutifyapp.data.repository.LocalDatabaseDebugRepository
import com.team695.scoutifyapp.data.repository.LocalDatabaseDebugSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isLoading: Boolean = true,
    val isLoadingRows: Boolean = false,
    val snapshot: LocalDatabaseDebugSnapshot? = null,
    val selectedTableName: String? = null,
    val loadedRows: List<com.team695.scoutifyapp.data.repository.LocalDatabaseDebugRow> = emptyList(),
    val rowSearchQuery: String = "",
    val errorMessage: String? = null,
)

class SettingsViewModel(
    private val localDatabaseDebugRepository: LocalDatabaseDebugRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        refreshDatabaseSnapshot()
    }

    fun refreshDatabaseSnapshot() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching {
                localDatabaseDebugRepository.loadSnapshot()
            }.onSuccess { snapshot ->
                val selectedTableName = snapshot.tables
                    .firstOrNull { it.name == _uiState.value.selectedTableName }
                    ?.name
                    ?: snapshot.tables.firstOrNull()?.name

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        snapshot = snapshot,
                        selectedTableName = selectedTableName,
                        loadedRows = emptyList(),
                        errorMessage = null
                    )
                }

                loadRowsForTable(selectedTableName)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoadingRows = false,
                        loadedRows = emptyList(),
                        errorMessage = error.message ?: "Failed to read the local database."
                    )
                }
            }
        }
    }

    fun selectTable(tableName: String) {
        _uiState.update {
            it.copy(
                selectedTableName = tableName,
                loadedRows = emptyList()
            )
        }
        loadRowsForTable(tableName)
    }

    fun updateRowSearchQuery(query: String) {
        _uiState.update {
            it.copy(rowSearchQuery = query)
        }
    }

    private fun loadRowsForTable(tableName: String?) {
        if (tableName == null) {
            _uiState.update {
                it.copy(
                    isLoadingRows = false,
                    loadedRows = emptyList()
                )
            }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    isLoadingRows = true,
                    errorMessage = null
                )
            }

            runCatching {
                localDatabaseDebugRepository.loadRowsForTable(tableName = tableName)
            }.onSuccess { rows ->
                _uiState.update { currentState ->
                    if (currentState.selectedTableName != tableName) {
                        currentState
                    } else {
                        currentState.copy(
                            isLoadingRows = false,
                            loadedRows = rows,
                            errorMessage = null
                        )
                    }
                }
            }.onFailure { error ->
                _uiState.update { currentState ->
                    if (currentState.selectedTableName != tableName) {
                        currentState
                    } else {
                        currentState.copy(
                            isLoadingRows = false,
                            loadedRows = emptyList(),
                            errorMessage = error.message ?: "Failed to read rows for $tableName."
                        )
                    }
                }
            }
        }
    }
}
