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
    val snapshot: LocalDatabaseDebugSnapshot? = null,
    val selectedTableName: String? = null,
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
                _uiState.update { currentState ->
                    val selectedTableName = snapshot.tables
                        .firstOrNull { it.name == currentState.selectedTableName }
                        ?.name
                        ?: snapshot.tables.firstOrNull()?.name

                    currentState.copy(
                        isLoading = false,
                        snapshot = snapshot,
                        selectedTableName = selectedTableName,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to read the local database."
                    )
                }
            }
        }
    }

    fun selectTable(tableName: String) {
        _uiState.update {
            it.copy(selectedTableName = tableName)
        }
    }

    fun updateRowSearchQuery(query: String) {
        _uiState.update {
            it.copy(rowSearchQuery = query)
        }
    }
}
