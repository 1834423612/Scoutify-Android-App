package com.team695.scoutifyapp.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team695.scoutifyapp.data.api.NetworkMonitor
import com.team695.scoutifyapp.data.api.model.Task
import com.team695.scoutifyapp.data.api.model.Match
import com.team695.scoutifyapp.data.api.model.TaskType
import com.team695.scoutifyapp.data.api.model.User
import com.team695.scoutifyapp.data.api.service.MatchService
import com.team695.scoutifyapp.data.api.service.TaskService
import com.team695.scoutifyapp.data.repository.MatchRepository
import com.team695.scoutifyapp.data.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

data class TabState(
    val selectedTab: Int = 0,
)

class HomeViewModel(
    private val taskRepository: TaskRepository,
    private val matchRepository: MatchRepository,
    private val networkMonitor: NetworkMonitor,

    ) : ViewModel() {
    private val _tabState = MutableStateFlow(TabState())
    val tabState: StateFlow<TabState> = _tabState

    private val _tasksState = taskRepository.tasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    val tasksState: StateFlow<List<Task>?> = _tasksState
    private val _matchState = matchRepository.matches
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    val matchState: StateFlow<List<Match>?> = _matchState

    init {
        viewModelScope.launch {
            while (isActive) {
                retryFetchUntilSuccess()
                delay(5.minutes)
            }
        }
    }

    private suspend fun retryFetchUntilSuccess() {
        withContext(Dispatchers.IO) {
            var matchResult = matchRepository.fetchMatches()
            var taskResult = taskRepository.fetchTasks()

            var duration = 10.seconds

            while (matchResult.isFailure || taskResult.isFailure) {
                networkMonitor.isConnected.first { it }

                delay(duration)

                if (matchResult.isFailure) {
                    matchResult = matchRepository.fetchMatches()
                }

                if (taskResult.isFailure) {
                    taskResult = taskRepository.fetchTasks()
                }

                duration = duration.coerceAtMost(40.seconds)
            }
        }
    }

    fun selectTab(index: Int) {
        _tabState.update { currentState ->
            currentState.copy(selectedTab = index)
        }
    }
}