package com.team695.scoutifyapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team695.scoutifyapp.data.repository.PitFormDataProvider
import com.team695.scoutifyapp.data.types.PitFormField
import com.team695.scoutifyapp.data.types.PitFormState
import com.team695.scoutifyapp.data.types.PitFormSubmission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel for managing pit scouting form state
 */
class PitScoutingViewModel : ViewModel() {
    
    private val _formState = MutableStateFlow(
        PitFormState(
            formId = UUID.randomUUID().toString(),
            eventName = "2025_JOHNSON",
            formVersion = "2025.4.15_PROD_ED6"
        )
    )
    val formState: StateFlow<PitFormState> = _formState.asStateFlow()
    
    init {
        loadFormFields()
    }
    
    /**
     * Load form fields from data provider
     * In production, this would fetch from API
     */
    private fun loadFormFields() {
        viewModelScope.launch {
            _formState.update { state ->
                state.copy(
                    isLoading = true,
                    error = null
                )
            }
            
            try {
                val fields = PitFormDataProvider.getDefaultFormFields()
                _formState.update { state ->
                    state.copy(
                        fields = fields,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _formState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = "Failed to load form: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Update a field value
     */
    fun updateFieldValue(fieldIndex: Int, value: Any?) {
        _formState.update { state ->
            val newFieldValues = state.fieldValues.toMutableMap()
            newFieldValues[fieldIndex] = value
            
            // Clear validation error for this field
            val newValidationErrors = state.validationErrors.toMutableMap()
            newValidationErrors.remove(fieldIndex)
            
            state.copy(
                fieldValues = newFieldValues,
                validationErrors = newValidationErrors
            )
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
    
    /**
     * Validate the form
     * Returns true if valid, false otherwise
     */
    fun validateForm(): Boolean {
        val state = _formState.value
        val errors = mutableMapOf<Int, String>()
        
        state.fields.forEach { field ->
            if (field.required) {
                val value = state.fieldValues[field.originalIndex]
                
                when {
                    value == null -> {
                        errors[field.originalIndex] = "This field is required"
                    }
                    value is String && value.isBlank() -> {
                        errors[field.originalIndex] = "This field is required"
                    }
                    value is List<*> && value.isEmpty() -> {
                        errors[field.originalIndex] = "Please select at least one option"
                    }
                }
            }
        }
        
        _formState.update { it.copy(validationErrors = errors) }
        return errors.isEmpty()
    }
    
    /**
     * Clear all form data
     */
    fun clearForm() {
        _formState.update { state ->
            state.copy(
                fieldValues = emptyMap(),
                validationErrors = emptyMap(),
                teamNumber = ""
            )
        }
    }
    
    /**
     * Save form as draft
     */
    fun saveDraft(): PitFormSubmission? {
        val state = _formState.value
        
        if (state.teamNumber.isBlank()) {
            _formState.update { it.copy(error = "Team number is required") }
            return null
        }
        
        return createSubmission(isDraft = true)
    }
    
    /**
     * Submit the form
     */
    fun submitForm(): PitFormSubmission? {
        if (!validateForm()) {
            _formState.update { it.copy(error = "Please fill in all required fields") }
            return null
        }
        
        val state = _formState.value
        if (state.teamNumber.isBlank()) {
            _formState.update { it.copy(error = "Team number is required") }
            return null
        }
        
        return createSubmission(isDraft = false)
    }
    
    /**
     * Create a submission object
     */
    private fun createSubmission(isDraft: Boolean): PitFormSubmission {
        val state = _formState.value
        
        // Map field indices to question text and values
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
    
    /**
     * Clear error message
     */
    fun clearError() {
        _formState.update { it.copy(error = null) }
    }
}
