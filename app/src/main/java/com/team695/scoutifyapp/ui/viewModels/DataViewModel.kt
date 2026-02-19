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
import com.team695.scoutifyapp.data.types.ShiftIntField
import com.team695.scoutifyapp.db.AppDatabase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@OptIn(FlowPreview::class)
class DataViewModel(private val repository: GameDetailRepository) : ViewModel() {

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

    val preGame: MutableMap<String, Any?> = mutableMapOf<String, Any?>(
        "starting_location" to 0,
        "robot_on_field" to false,
        "robot_preloaded" to false,
    ),

    val auton: MutableMap<String, Any?> = mutableMapOf<String, Any?>(
        "auton_path" to "",
        "auton_attempts_climb" to false,
        "auton_climb_success" to false,
        "auton_climb_position" to "",
        "auton_fuel_count" to 0,
    ),

    val transitionShift: MutableMap<String, Any?> = mutableMapOf<String, Any?>(
        "transition_cycling_time" to 0,
        "transition_stockpiling_time" to 0,
        "transition_defending_time" to 0,
        "transition_broken_time" to 0,
        "transition_first_active" to 0,
    ),

    val shift1: MutableMap<String, Any?> = mutableMapOf<String, Any?>(
        "shift1_cycling_time" to 0,
        "shift1_stockpiling_time" to 0,
        "shift1_defending_time" to 0,
        "shift1_broken_time" to 0,
    ),

    val shift2: MutableMap<String, Any?> = mutableMapOf<String, Any?>(
        "shift2_cycling_time" to 0,
        "shift2_stockpiling_time" to 0,
        "shift2_defending_time" to 0,
        "shift2_broken_time" to 0,
    ),

    val shift3: MutableMap<String, Any?> = mutableMapOf<String, Any?>(
        "shift3_cycling_time" to 0,
        "shift3_stockpiling_time" to 0,
        "shift3_defending_time" to 0,
        "shift3_broken_time" to 0,
    ),

    val shift4: MutableMap<String, Any?> = mutableMapOf<String, Any?>(
        "shift4_cycling_time" to 0,
        "shift4_stockpiling_time" to 0,
        "shift4_defending_time" to 0,
        "shift4_broken_time" to 0,
    ),

    val endgame: MutableMap<String, Any?> = mutableMapOf<String, Any?>(
        "endgame_cycling_time" to 0,
        "endgame_stockpiling_time" to 0,
        "endgame_defending_time" to 0,
        "endgame_broken_time" to 0,
        "endgame_attempts_climb" to 0,
        "endgame_climb_success" to 0,
        "endgame_climb_position" to "",
    ),

    val teleop: MutableMap<String, Any?> = mutableMapOf<String, Any?>(
        "teleop_fuel_count" to 0,
        "teleop_shoot_anywhere" to false,
        "teleop_shoot_while_moving" to false,
        "teleop_stockpile_neutral" to false,
        "teleop_stockpile_alliance" to false,
        "teleop_stockpile_cross_court" to false,
        "teleop_feed_outpost" to false,
        "teleop_receive_outpost" to false,
        "teleop_under_trench" to false,
        "teleop_over_trench" to false,
    ),
) {
    val preGameProgress = preGame.count{it.value != null}/preGame.size
    val autonProgress = auton.count{it.value != null}
    val transitionShiftProgress = transitionShift.count{it.value != null}
    val shift1Progress = shift1.count{it.value != null}
    val shift2Progress = shift2.count{it.value != null}
    val shift3Progress = shift3.count{it.value != null}
    val shift4Progress = shift4.count{it.value != null}
    val endgameProgress = endgame.count{it.value != null}
    val teleopProgress = teleop.count{it.value != null}
}

data class StandardShiftState(
    val intFields: Map<ShiftIntField, Int?> = ShiftIntField.entries.associateWith { null }
) {
    val progress: Float get() {
        val total = ShiftIntField.entries.size
        if (total == 0) return 0f
        return intFields.count { it.value != null }.toFloat() / total
    }
}


sealed class FormEvent {
    data class UpdateAutoCoral(val count: Int) : FormEvent()
    data class ToggleSectionFlag(val section: GameSection) : FormEvent()
}

enum class GameSection { PRE_GAME, AUTO, TELE_OP, POST_GAME }