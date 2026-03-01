package com.team695.scoutifyapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team695.scoutifyapp.data.repository.PitFormDataProvider
import com.team695.scoutifyapp.data.repository.PitScoutingRepository
import com.team695.scoutifyapp.data.types.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * State for the pit scouting screen with multi-tab support
 */
data class PitScoutingScreenState(
    val eventKey: String = "",
    val tabs: List<PitScoutingTab> = emptyList(),
    val selectedTabId: String? = null,
    val submissionMessages: Map<String, String> = emptyMap(), // tabId -> message
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for managing pit scouting form state with multi-tab support
 */
class PitScoutingViewModel(
    private val repository: PitScoutingRepository,
    private val eventKey: String = "2025_JOHNSON"
) : ViewModel() {
    
    private val _screenState = MutableStateFlow(
        PitScoutingScreenState(eventKey = eventKey)
    )
    val screenState: StateFlow<PitScoutingScreenState> = _screenState.asStateFlow()
    
    private val _formState = MutableStateFlow(
        PitFormState(
            formId = UUID.randomUUID().toString(),
            eventName = eventKey,
            formVersion = "2025.4.15_PROD_ED6"
        )
    )
    val formState: StateFlow<PitFormState> = _formState.asStateFlow()

    init {
        loadFormFields()
        loadTabsForEvent()
    }

    // ==================== Tab Management ====================

    /**
     * Load all tabs for the current event from local database
     */
    private fun loadTabsForEvent() {
        viewModelScope.launch {
            try {
                repository.getTabsForEvent(eventKey).collect { tabs ->
                    _screenState.update { state ->
                        val selectedTabId = state.selectedTabId ?: tabs.firstOrNull()?.tabId
                        state.copy(
                            tabs = tabs,
                            selectedTabId = selectedTabId
                        )
                    }
                }
            } catch (e: Exception) {
                _screenState.update { state ->
                    state.copy(error = "Failed to load tabs: ${e.message}")
                }
            }
        }
    }

    /**
     * Create a new pit scouting tab for a team
     */
    fun createNewTab(teamNumber: String) {
        viewModelScope.launch {
            try {
                _screenState.update { it.copy(isLoading = true) }
                val formFields = PitFormDataProvider.getDefaultFormFields()
                val newTab = repository.createNewTab(teamNumber, eventKey, formFields)
                switchToTab(newTab.tabId)
                _screenState.update { state ->
                    state.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _screenState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = "Failed to create tab: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Switch to a different tab
     */
    fun switchToTab(tabId: String) {
        viewModelScope.launch {
            try {
                repository.getTabById(tabId).collect { tab ->
                    if (tab != null) {
                        _screenState.update { state ->
                            state.copy(selectedTabId = tabId)
                        }
                        
                        _formState.update { formState ->
                            formState.copy(
                                formId = tab.formId,
                                teamNumber = tab.teamNumber,
                                fieldValues = tab.fieldValues,
                                fields = PitFormDataProvider.getDefaultFormFields()
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _screenState.update { state ->
                    state.copy(error = "Failed to switch tab: ${e.message}")
                }
            }
        }
    }

    /**
     * Close/delete a tab
     */
    fun closeTab(tabId: String) {
        viewModelScope.launch {
            try {
                repository.deleteTab(tabId)
                if (_screenState.value.selectedTabId == tabId) {
                    val remainingTabs = _screenState.value.tabs.filter { it.tabId != tabId }
                    if (remainingTabs.isNotEmpty()) {
                        switchToTab(remainingTabs.first().tabId)
                    } else {
                        _screenState.update { state ->
                            state.copy(selectedTabId = null)
                        }
                    }
                }
            } catch (e: Exception) {
                _screenState.update { state ->
                    state.copy(error = "Failed to close tab: ${e.message}")
                }
            }
        }
    }

    // ==================== Form Field Management ====================

    /**
     * Load form fields from data provider
     */
    private fun loadFormFields() {
        viewModelScope.launch {
            _formState.update { state ->
                state.copy(isLoading = true, error = null)
            }
            try {
                val fields = PitFormDataProvider.getDefaultFormFields()
                _formState.update { state ->
                    state.copy(fields = fields, isLoading = false)
                }
            } catch (e: Exception) {
                _formState.update { state ->
                    state.copy(isLoading = false, error = "Failed to load form: ${e.message}")
                }
            }
        }
    }

    /**
     * Update a field value and save it to the database
     */
    fun updateFieldValue(fieldIndex: Int, value: Any?) {
        val screenState = _screenState.value
        val tabId = screenState.selectedTabId ?: return

        viewModelScope.launch {
            try {
                repository.updateTabFieldValue(tabId, fieldIndex, value)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        _formState.update { state ->
            val newFieldValues = state.fieldValues.toMutableMap()
            newFieldValues[fieldIndex] = value
            val newValidationErrors = state.validationErrors.toMutableMap()
            newValidationErrors.remove(fieldIndex)
            state.copy(fieldValues = newFieldValues, validationErrors = newValidationErrors)
        }
    }

    /**
     * Set the team number
     */
    fun setTeamNumber(teamNumber: String) {
        _formState.update { state ->
            state.copy(teamNumber = teamNumber)
        }
    }

    // ==================== Validation & Submission ====================

    /**
     * Validate the form
     */
    fun validateForm(): Boolean {
        val state = _formState.value
        val errors = mutableMapOf<Int, String>()
        state.fields.forEach { field ->
            if (field.required) {
                val value = state.fieldValues[field.originalIndex]
                when {
                    value == null -> errors[field.originalIndex] = "This field is required"
                    value is String && value.isBlank() -> errors[field.originalIndex] = "This field is required"
                    value is List<*> && value.isEmpty() -> errors[field.originalIndex] = "Please select at least one option"
                }
            }
        }
        _formState.update { it.copy(validationErrors = errors) }
        return errors.isEmpty()
    }

    /**
     * Submit the current tab form
     */
    fun submitForm(): PitFormSubmission? {
        if (!validateForm()) {
            setSubmissionMessage("Please fill in all required fields")
            return null
        }

        val state = _formState.value
        val screenState = _screenState.value
        val tabId = screenState.selectedTabId ?: return null

        if (state.teamNumber.isBlank()) {
            setSubmissionMessage("Team number is required")
            return null
        }

        viewModelScope.launch {
            try {
                val result = repository.submitTab(tabId)
                result.onSuccess {
                    setSubmissionMessage("Form submitted successfully for team ${state.teamNumber}")
                }.onFailure { error ->
                    setSubmissionMessage("Submission failed: ${error.message}")
                }
            } catch (e: Exception) {
                setSubmissionMessage("Error: ${e.message}")
            }
        }

        return createSubmission(isDraft = false)
    }

    /**
     * Save form as draft
     */
    fun saveDraft(): PitFormSubmission? {
        val state = _formState.value
        val screenState = _screenState.value
        val tabId = screenState.selectedTabId ?: return null

        if (state.teamNumber.isBlank()) {
            setSubmissionMessage("Team number is required")
            return null
        }

        viewModelScope.launch {
            try {
                repository.saveDraft(tabId)
                setSubmissionMessage("Draft saved for team ${state.teamNumber}")
            } catch (e: Exception) {
                setSubmissionMessage("Error saving draft: ${e.message}")
            }
        }

        return createSubmission(isDraft = true)
    }

    /**
     * Clear all form data in current tab
     */
    fun clearForm() {
        val screenState = _screenState.value
        val tabId = screenState.selectedTabId ?: return

        viewModelScope.launch {
            try {
                val newTab = repository.createNewTab(
                    teamNumber = "",
                    eventKey = eventKey,
                    formFields = PitFormDataProvider.getDefaultFormFields()
                )
                switchToTab(newTab.tabId)
                setSubmissionMessage("Form cleared")
            } catch (e: Exception) {
                setSubmissionMessage("Error clearing form: ${e.message}")
            }
        }
    }

    // ==================== Utility Methods ====================

    /**
     * Set a temporary submission message (toast-like message)
     */
    private fun setSubmissionMessage(message: String) {
        val tabId = _screenState.value.selectedTabId
        if (tabId != null) {
            _screenState.update { state ->
                state.copy(
                    submissionMessages = state.submissionMessages.toMutableMap().apply {
                        put(tabId, message)
                    }
                )
            }
            viewModelScope.launch {
                kotlinx.coroutines.delay(3000)
                _screenState.update { state ->
                    state.copy(
                        submissionMessages = state.submissionMessages.toMutableMap().apply {
                            remove(tabId)
                        }
                    )
                }
            }
        }
    }

    /**
     * Manually clear submission message for a tab
     */
    fun clearSubmissionMessage(tabId: String) {
        _screenState.update { state ->
            state.copy(
                submissionMessages = state.submissionMessages.toMutableMap().apply {
                    remove(tabId)
                }
            )
        }
    }

    /**
     * Create a submission object
     */
    private fun createSubmission(isDraft: Boolean): PitFormSubmission {
        val state = _formState.value
        val responses = state.fields.associate { field ->
            field.question to state.fieldValues[field.originalIndex]
        }
        return PitFormSubmission(
            formId = state.formId,
            eventName = state.eventName,
            teamNumber = state.teamNumber,
            responses = responses,
            isDraft = isDraft
        )
    }
}
