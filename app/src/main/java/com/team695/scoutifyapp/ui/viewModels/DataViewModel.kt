package com.team695.scoutifyapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team695.scoutifyapp.data.api.model.GameDetails
import com.team695.scoutifyapp.data.api.model.Task
import com.team695.scoutifyapp.data.api.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.team695.scoutifyapp.data.repository.GameDetailRepository
import com.team695.scoutifyapp.data.repository.TaskRepository
import com.team695.scoutifyapp.data.types.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.plus


@OptIn(FlowPreview::class)
class DataViewModel(
    private val gameDetailRepository: GameDetailRepository,
    private val taskRepository: TaskRepository,
    private val taskId: Int,
) : ViewModel() {
    private val _formState = MutableStateFlow(
        GameFormState(
            matchNum = -1,
            teamNumber = "LOADING",
            gameDetails = GameDetails()
        )
    )
    val formState: StateFlow<GameFormState> = _formState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val gameDetails: GameDetails = gameDetailRepository.getGameDetailsByTaskId(taskId)
            val taskResult: Result<Task> = taskRepository.getTaskById(taskId)
            val task: Task? = taskResult.getOrNull()

            _formState.update {
                it.copy(
                    teamNumber = task?.teamNum ?: "FAILED",
                    matchNum = task?.matchNum ?: -2,
                    gameDetails = gameDetails
                )
            }
        }

        _formState
            .debounce(2000L)
            .distinctUntilChanged() // Only save if the state actually changed
            .onEach { currentFormState: GameFormState ->
                //When saving works, use this!
                //gameDetailRepository.updateDbFromGameDetails(convertFormToGameDetails(currentFormState))
            }
            .launchIn(viewModelScope) // Run this in the background tied to the ViewModel's lifecycle
    }
}