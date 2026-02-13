package com.team695.scoutifyapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team695.scoutifyapp.data.api.model.Task
import com.team695.scoutifyapp.data.api.model.Match
import com.team695.scoutifyapp.data.api.service.MatchService
import com.team695.scoutifyapp.data.api.service.TaskService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val selectedTab: Int = 0,
    val incompleteTasks: List<Task> = emptyList(),
    val completeTasks: List<Task> = emptyList(),
    val matchList: List<Match> = emptyList()
)

class HomeViewModel(
    private val matchService: MatchService,
    private val taskService: TaskService
): ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadTasks()
        fetchMatches()
    }

    fun fetchMatches() {
        viewModelScope.launch {
            try {
                val matchesRes = matchService.listMatches()
                //_matches.value = matchesRes.string()
            } catch (e: Exception) {
                println("Error fetching matches: ${e.message}")
            }
        }
    }

    private fun loadTasks() {
        // Mock data for now
        viewModelScope.launch {
            try {
                val tasks = taskService.getTasks()
                _uiState.update { currentState ->
                    currentState.copy(
                        incompleteTasks = tasks.filter { !it.isDone },
                        completeTasks = tasks.filter { it.isDone },
                    )
                }
            } catch (e: Exception) {
                println("Error fetching tasks: ${e.message}")
            }
            try {
                val matches = matchService.listMatches()
                _uiState.update { currentState ->
                    currentState.copy(
                        matchList = matches
                    )
                }
            } catch (e: Exception) {
                println("Error fetching match listing: ${e.message}")

            }
        }

    }

    fun selectTab(index: Int) {
        _uiState.update { currentState ->
            currentState.copy(selectedTab = index)
        }
    }
}