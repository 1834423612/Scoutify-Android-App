package com.team695.scoutifyapp.ui.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team695.scoutifyapp.data.api.model.GameConstantsStore
import com.team695.scoutifyapp.data.repository.PitFormDataProvider
import com.team695.scoutifyapp.data.repository.PitScoutingRepository
import com.team695.scoutifyapp.data.types.FieldType
import com.team695.scoutifyapp.data.types.PitFieldValue
import com.team695.scoutifyapp.data.types.PitFormField
import com.team695.scoutifyapp.data.types.PitFormState
import com.team695.scoutifyapp.data.types.PitImageAsset
import com.team695.scoutifyapp.data.types.PitScoutingTab
import com.team695.scoutifyapp.data.types.valueAsList
import com.team695.scoutifyapp.data.types.valueAsText
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PitScoutingViewModel(
    private val repository: PitScoutingRepository
) : ViewModel() {
    private val eventKey = buildEventKey()
    private val _formState = MutableStateFlow(
        PitFormState(
            eventId = eventKey,
            eventDisplayName = eventKey,
            formVersion = PitFormDataProvider.FORM_VERSION,
            isLoading = true
        )
    )
    val formState: StateFlow<PitFormState> = _formState.asStateFlow()

    private var selectedTabId: String? = null
    private var teamsJob: Job? = null
    private var starterTabCreated = false

    init {
        observeTabs()
        refreshSupportingData()
    }

    private fun observeTabs() {
        viewModelScope.launch {
            repository.getTabsForEvent(eventKey).collect { tabs ->
                val versionMismatch = tabs.any { it.formVersion != PitFormDataProvider.FORM_VERSION }
                if (tabs.isEmpty() && !starterTabCreated && !versionMismatch) {
                    starterTabCreated = true
                    repository.createNewTab(eventKey)
                    return@collect
                }

                val activeTab = tabs.firstOrNull { it.tabId == selectedTabId } ?: tabs.firstOrNull()
                selectedTabId = activeTab?.tabId
                _formState.update { state ->
                    state.copy(
                        tabs = tabs,
                        activeTab = activeTab,
                        formVersion = PitFormDataProvider.FORM_VERSION,
                        versionMismatch = versionMismatch,
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
    }

    fun refreshSupportingData() {
        viewModelScope.launch {
            val assignments = repository.fetchAssignments(eventKey)
            val completedTeams = repository.fetchCompletedTeams(eventKey)
            _formState.update { state ->
                state.copy(assignments = assignments, completedTeams = completedTeams)
            }
        }
    }

    fun switchToTab(tabId: String) {
        selectedTabId = tabId
        _formState.update { state ->
            state.copy(activeTab = state.tabs.firstOrNull { it.tabId == tabId })
        }
    }

    fun createNewTab(teamNumber: String = "") {
        viewModelScope.launch {
            val newTab = repository.createNewTab(eventKey, teamNumber)
            selectedTabId = newTab.tabId
        }
    }

    fun closeTab(tabId: String) {
        viewModelScope.launch {
            repository.deleteTab(tabId)
            if (selectedTabId == tabId) {
                selectedTabId = null
            }
        }
    }

    fun resetForVersionChange() {
        viewModelScope.launch {
            repository.deleteTabsForEvent(eventKey)
            starterTabCreated = false
        }
    }

    fun clearCurrentTab() {
        val activeTab = _formState.value.activeTab ?: return
        viewModelScope.launch {
            repository.clearTab(activeTab.tabId)
            showBanner("Current tab cleared")
        }
    }

    fun saveDraft() {
        val activeTab = _formState.value.activeTab ?: return
        viewModelScope.launch {
            repository.saveDraft(activeTab.tabId)
            showBanner("Draft saved locally")
        }
    }

    fun updateTextField(fieldIndex: Int, value: String) {
        val activeTab = _formState.value.activeTab ?: return
        viewModelScope.launch {
            repository.updateField(activeTab.tabId, fieldIndex) { field ->
                field.copy(value = if (value.isBlank()) PitFieldValue.Empty else PitFieldValue.TextValue(value), error = null)
            }
        }
        if (fieldIndex == 0) {
            loadTeamSuggestions(value)
        }
    }

    fun updateOtherValue(fieldIndex: Int, value: String) {
        val activeTab = _formState.value.activeTab ?: return
        viewModelScope.launch {
            repository.updateField(activeTab.tabId, fieldIndex) { field ->
                field.copy(otherValue = value, error = null)
            }
        }
    }

    fun selectRadioOption(fieldIndex: Int, selectedValue: String) {
        val activeTab = _formState.value.activeTab ?: return
        viewModelScope.launch {
            repository.updateField(activeTab.tabId, fieldIndex) { field ->
                field.copy(value = PitFieldValue.TextValue(selectedValue), error = null)
            }
        }
    }

    fun toggleCheckboxValue(fieldIndex: Int, optionValue: String) {
        val activeTab = _formState.value.activeTab ?: return
        viewModelScope.launch {
            repository.updateField(activeTab.tabId, fieldIndex) { field ->
                val currentValues = field.valueAsList().toMutableList()
                if (currentValues.contains(optionValue)) {
                    currentValues.remove(optionValue)
                } else {
                    currentValues.add(optionValue)
                }
                field.copy(value = PitFieldValue.MultiValue(currentValues), error = null)
            }
        }
    }

    fun selectTeam(teamNumber: String) {
        updateTextField(0, teamNumber)
        _formState.update { it.copy(teamSuggestions = emptyList()) }
    }

    fun selectAssignedTeam(teamNumber: String) {
        selectTeam(teamNumber)
        showBanner("Loaded Team $teamNumber into the current tab")
    }

    fun loadTeamSuggestions(query: String) {
        teamsJob?.cancel()
        if (query.isBlank()) {
            _formState.update { it.copy(teamSuggestions = emptyList()) }
            return
        }

        teamsJob = viewModelScope.launch {
            val suggestions = repository.getTeamSuggestions(query)
            _formState.update { state -> state.copy(teamSuggestions = suggestions) }
        }
    }

    fun addImages(bucket: String, uris: List<Uri>) {
        val activeTab = _formState.value.activeTab ?: return
        if (uris.isEmpty()) return
        viewModelScope.launch {
            repository.addImages(activeTab.tabId, bucket, uris)
            showBanner("${uris.size} image(s) added")
        }
    }

    fun removeImage(bucket: String, image: PitImageAsset) {
        val activeTab = _formState.value.activeTab ?: return
        viewModelScope.launch {
            repository.removeImage(activeTab.tabId, bucket, image)
            showBanner("Image removed")
        }
    }

    fun submitCurrentTab() {
        val activeTab = _formState.value.activeTab ?: return
        val validationErrors = validate(activeTab)
        if (validationErrors.isNotEmpty()) {
            applyValidationErrors(activeTab.tabId, validationErrors)
            showBanner("Please complete the required pit scouting fields")
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(isSubmitting = true) }
            val result = repository.submitTab(activeTab.tabId)
            _formState.update { it.copy(isSubmitting = false) }
            result.onSuccess { submittedNow ->
                if (submittedNow) {
                    showBanner("Pit scouting submitted successfully")
                    refreshSupportingData()
                } else {
                    showBanner("No network. Submission queued and will retry automatically")
                }
            }.onFailure { error ->
                showBanner(error.message ?: "Pit scouting submission failed")
            }
        }
    }

    fun dismissBanner() {
        _formState.update { it.copy(syncBanner = null) }
    }

    private fun applyValidationErrors(tabId: String, validationErrors: Map<Int, String>) {
        viewModelScope.launch {
            validationErrors.forEach { (index, message) ->
                repository.updateField(tabId, index) { field -> field.copy(error = message) }
            }
        }
    }

    private fun validate(tab: PitScoutingTab): Map<Int, String> {
        return tab.fields.mapNotNull { field ->
            val error = when {
                !field.required -> null
                field.type == FieldType.CHECKBOX && field.valueAsList().isEmpty() -> "This field is required"
                field.type != FieldType.CHECKBOX && field.valueAsText().isBlank() -> "This field is required"
                needsOtherValue(field) && field.otherValue.isBlank() -> "Please specify the other option"
                else -> null
            }
            if (error == null) null else field.originalIndex to error
        }.toMap()
    }

    private fun needsOtherValue(field: PitFormField): Boolean {
        return field.valueAsText() == "Other" || field.valueAsList().contains("Other")
    }

    private fun showBanner(message: String) {
        _formState.update { it.copy(syncBanner = message) }
    }

    private fun buildEventKey(): String {
        val year = GameConstantsStore.constants.frc_season_master_sm_year
        val eventCode = GameConstantsStore.constants.competition_master_cm_event_code
        if (year == 0 || eventCode.isBlank()) {
            return "2026_MNWI"
        }
        return "${year}_${eventCode.uppercase()}"
    }
}
