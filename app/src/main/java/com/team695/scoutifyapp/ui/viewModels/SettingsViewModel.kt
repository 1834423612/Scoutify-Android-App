package com.team695.scoutifyapp.ui.viewModels

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.core.content.pm.PackageInfoCompat
import com.team695.scoutifyapp.BuildConfig
import com.team695.scoutifyapp.data.repository.LocalDatabaseDebugRepository
import com.team695.scoutifyapp.data.repository.LocalDatabaseDebugRow
import com.team695.scoutifyapp.data.repository.LocalDatabaseDebugSnapshot
import com.team695.scoutifyapp.data.repository.UserAccessProfile
import com.team695.scoutifyapp.data.repository.UserRepository
import com.team695.scoutifyapp.ui.extensions.androidID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isLoading: Boolean = false,
    val isLoadingRows: Boolean = false,
    val snapshot: LocalDatabaseDebugSnapshot? = null,
    val selectedTableName: String? = null,
    val loadedRows: List<LocalDatabaseDebugRow> = emptyList(),
    val pageIndex: Int = 0,
    val pageSize: Int = 100,
    val rowSearchQuery: String = "",
    val errorMessage: String? = null,
    val displayName: String = "",
    val username: String = "",
    val email: String = "",
    val localAndroidId: String = "",
    val registeredAndroidId: String = "",
    val appVersionName: String = "",
    val appVersionCode: String = "",
    val packageName: String = "",
    val buildTypeLabel: String = "",
    val manufacturer: String = "",
    val brand: String = "",
    val model: String = "",
    val androidVersion: String = "",
    val sdkInt: Int = 0,
    val groups: List<String> = emptyList(),
    val roles: List<String> = emptyList(),
    val permissions: List<String> = emptyList(),
    val isAdmin: Boolean = false,
    val canManageLocalDatabase: Boolean = false,
    val isDebugBuild: Boolean = BuildConfig.DEBUG,
) {
    val deviceDisplayName: String
        get() = listOf(manufacturer, model)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { Build.DEVICE }

    val hasBoundDeviceId: Boolean
        get() = registeredAndroidId.isNotBlank()

    val pageCount: Int
        get() {
            val totalRows = snapshot?.tables
                ?.firstOrNull { it.name == selectedTableName }
                ?.rowCount
                ?: 0
            return if (totalRows <= 0) 1 else ((totalRows - 1) / pageSize) + 1
        }

    val deviceIdMatches: Boolean?
        get() {
            if (localAndroidId.isBlank() || registeredAndroidId.isBlank()) {
                return null
            }
            return localAndroidId == registeredAndroidId
        }
}

class SettingsViewModel(
    private val localDatabaseDebugRepository: LocalDatabaseDebugRepository,
    private val userRepository: UserRepository,
    private val appContext: Context,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeSettingsMetadata()
    }

    fun ensureDatabaseSnapshotLoaded() {
        val state = _uiState.value
        if (!state.canManageLocalDatabase) {
            return
        }
        if (state.snapshot == null && !state.isLoading) {
            refreshDatabaseSnapshot()
        }
    }

    fun refreshDatabaseSnapshot() {
        if (!_uiState.value.canManageLocalDatabase) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isLoadingRows = false,
                    loadedRows = emptyList(),
                    errorMessage = "Administrator permission is required to inspect the local database."
                )
            }
            return
        }

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
                        pageIndex = 0,
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
                pageIndex = 0,
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

    fun goToNextPage() {
        val current = _uiState.value
        if (current.pageIndex + 1 >= current.pageCount) {
            return
        }
        _uiState.update { it.copy(pageIndex = it.pageIndex + 1, loadedRows = emptyList()) }
        loadRowsForTable(_uiState.value.selectedTableName)
    }

    fun goToPreviousPage() {
        val current = _uiState.value
        if (current.pageIndex <= 0) {
            return
        }
        _uiState.update { it.copy(pageIndex = (it.pageIndex - 1).coerceAtLeast(0), loadedRows = emptyList()) }
        loadRowsForTable(_uiState.value.selectedTableName)
    }

    fun goToPage(pageIndex: Int) {
        val current = _uiState.value
        val normalizedPageIndex = pageIndex.coerceIn(0, (current.pageCount - 1).coerceAtLeast(0))
        if (normalizedPageIndex == current.pageIndex) {
            return
        }
        _uiState.update { it.copy(pageIndex = normalizedPageIndex, loadedRows = emptyList()) }
        loadRowsForTable(_uiState.value.selectedTableName)
    }

    fun updatePageSize(pageSize: Int) {
        val normalizedPageSize = pageSize.coerceIn(25, 500)
        _uiState.update {
            it.copy(
                pageSize = normalizedPageSize,
                pageIndex = 0,
                loadedRows = emptyList()
            )
        }
        loadRowsForTable(_uiState.value.selectedTableName)
    }

    private fun observeSettingsMetadata() {
        viewModelScope.launch(Dispatchers.IO) {
            val packageInfo = appContext.packageManager.getPackageInfo(
                appContext.packageName,
                PackageManager.PackageInfoFlags.of(0)
            )
            val accessProfile = userRepository.getAccessProfile()
            val localAndroidId = runCatching { appContext.androidID }.getOrDefault("").orEmpty()

            userRepository.currentUser.collectLatest { user ->
                _uiState.update {
                    it.copy(
                        displayName = user?.displayName.orEmpty(),
                        username = user?.name.orEmpty(),
                        email = user?.email.orEmpty(),
                        localAndroidId = localAndroidId,
                        registeredAndroidId = user?.androidID.orEmpty(),
                        appVersionName = packageInfo.versionName ?: BuildConfig.VERSION_NAME,
                        appVersionCode = PackageInfoCompat.getLongVersionCode(packageInfo).toString(),
                        packageName = appContext.packageName,
                        buildTypeLabel = if (BuildConfig.DEBUG) "Debug" else "Release",
                        manufacturer = Build.MANUFACTURER.orEmpty(),
                        brand = Build.BRAND.orEmpty(),
                        model = Build.MODEL.orEmpty(),
                        androidVersion = Build.VERSION.RELEASE.orEmpty(),
                        sdkInt = Build.VERSION.SDK_INT,
                        groups = accessProfile.groups,
                        roles = accessProfile.roles,
                        permissions = accessProfile.permissions,
                        isAdmin = accessProfile.isAdmin,
                        canManageLocalDatabase = accessProfile.canManageDatabase
                    )
                }
            }
        }
    }

    private fun loadRowsForTable(tableName: String?) {
        if (tableName == null || !_uiState.value.canManageLocalDatabase) {
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
                val currentState = _uiState.value
                localDatabaseDebugRepository.loadRowsForTable(
                    tableName = tableName,
                    limit = currentState.pageSize,
                    offset = currentState.pageIndex * currentState.pageSize
                )
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
