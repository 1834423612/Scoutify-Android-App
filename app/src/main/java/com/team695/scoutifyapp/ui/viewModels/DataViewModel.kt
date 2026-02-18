package com.team695.scoutifyapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team695.scoutifyapp.data.api.model.Task
import com.team695.scoutifyapp.data.api.model.Match
import com.team695.scoutifyapp.data.api.service.MatchService
import com.team695.scoutifyapp.data.api.service.TaskService
import com.team695.scoutifyapp.data.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.team695.scoutifyapp.data.api.model.GameDetails
import com.team695.scoutifyapp.data.api.model.TaskType
import com.team695.scoutifyapp.data.api.model.User
import com.team695.scoutifyapp.data.repository.GameDetailRepository
import com.team695.scoutifyapp.db.AppDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class DataViewModel(private val repository: GameDetailRepository, private val db: AppDatabase) : ViewModel() {

    private val _uiState = MutableStateFlow(GameFormState())
    val uiState: StateFlow<GameFormState> = _uiState.asStateFlow()

    init {
        _uiState
            .debounce(2000L)
            .distinctUntilChanged() // Only save if the state actually changed
            .onEach { currentFormState: GameFormState ->
                saveToDatabase(currentFormState)
            }
            .launchIn(viewModelScope) // Run this in the background tied to the ViewModel's lifecycle
    }

    // Handle all UI actions through a single entry point
    fun onEvent(event: FormEvent) {
        when (event) {
            is FormEvent.UpdateAutoCoral -> {
                _uiState.update { it.copy(auto = it.auto.copy(coralScored = event.count)) }
                //saveDataDebounced()
            }
            is FormEvent.ToggleSectionFlag -> {
                // Logic to toggle the specific section's flag
                //saveDataDebounced()
            }
            // Add other events...
        }
    }


    suspend fun saveToDatabase(gameFormState: GameFormState) {

    }
}

data class GameFormState(
    val matchId: Int = 0,
    val teamNumber: Int = 0,

    val auto: AutoData = AutoData(),

) {
    val autoProgress: Float get() = auto.calculateProgress()
}



data class AutoData(
    val isFlagged: Boolean = false,
    val coralScored: Int? = null, // Null means unfilled, useful for progress
    val algaeRemoved: Int? = null
) {
    fun calculateProgress(): Float {
        var filled = 0
        val totalFields = 2 // Update as you add fields
        if (coralScored != null) filled++
        if (algaeRemoved != null) filled++
        return filled.toFloat() / totalFields
    }
}

// Represents everything the user can DO on the screen
sealed class FormEvent {
    data class UpdateAutoCoral(val count: Int) : FormEvent()
    data class ToggleSectionFlag(val section: GameSection) : FormEvent()
    // ...
}

enum class GameSection { PRE_GAME, AUTO, TELE_OP, POST_GAME }