package com.team695.scoutifyapp.ui.viewModels

import androidx.lifecycle.ViewModel
import com.team695.scoutifyapp.data.Task
import com.team695.scoutifyapp.data.TaskType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TaskService {
    fun getTasks(): List<Task> {
        return listOf(
            Task(2, TaskType.SCOUTING, 3, "118", "01m", 0.25f, false, "PARTIAL"),
            Task(3, TaskType.SCOUTING, 4, "254", "03m", 1.0f, true, "COMPLETE"),
            Task(4, TaskType.SCOUTING, 5, "148", "02m", 0.0f, false, "INCOMPLETE"),
            Task(5, TaskType.SCOUTING, 6, "971", "01m", 1.0f, true, "COMPLETE")
        )
    }
}

data class TasksUiState(
    val selectedTab: Int = 0,
    val incompleteTasks: List<Task> = emptyList(),
    val doneTasks: List<Task> = emptyList()
)

class TasksViewModel(private val service: TaskService = TaskService()) : ViewModel() {
    private val _uiState = MutableStateFlow<TasksUiState>(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    init {
        // Load initial data
        loadTasks()
    }

    private fun loadTasks() {
        // Mock data for now
        val tasks = service.getTasks()
        _uiState.value = TasksUiState(
            incompleteTasks = tasks.filter { !it.isDone },
            doneTasks = tasks.filter { it.isDone }
        )
    }

    fun selectTab(index: Int) {
        _uiState.update { currentState ->
            currentState.copy(selectedTab = index)
        }
    }
}
