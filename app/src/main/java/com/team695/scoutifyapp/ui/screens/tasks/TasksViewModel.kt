package com.team695.scoutifyapp.ui.screens.tasks

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TaskRepository {
    fun getTasks(): List<Task> {
        return listOf(
            Task(1, 2, "695", "02m", 0.5f, false, "PARTIAL"),
            Task(2, 3, "118", "01m", 0.25f, false, "PARTIAL"),
            Task(3, 4, "254", "03m", 1.0f, true, "COMPLETE"),
            Task(4, 5, "148", "02m", 0.0f, false, "INCOMPLETE"),
            Task(5, 6, "971", "01m", 1.0f, true, "COMPLETE")
        )
    }
}

data class Task(
    val id: Int,
    val matchNum: Int,
    val teamNum: String,
    val time: String,
    val progress: Float, // 0.0 to 1.0
    val isDone: Boolean = false,
    val taskCompPercentString: String
)

data class TasksUiState(
    val selectedTab: Int = 0,
    val incompleteTasks: List<Task> = emptyList(),
    val doneTasks: List<Task> = emptyList()
)

class TasksViewModel(private val repository: TaskRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    init {
        // Load initial data
        loadTasks()
    }

    private fun loadTasks() {
        // Mock data for now
        val tasks = repository.getTasks()
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
