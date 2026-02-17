package com.team695.scoutifyapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team695.scoutifyapp.data.api.NetworkMonitor
import com.team695.scoutifyapp.data.repository.TaskRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes


// every 5 minutes checks that user is connected to wifi
// then calls fetchTasks() every 10 seconds until .isSuccessful
class TasksViewModel(
    private val networkMonitor: NetworkMonitor,
    private val repository: TaskRepository
) : ViewModel() {

    init {
        // start monitoring internet
        networkMonitor.startMonitoring()

        // start a coroutine which will perform the
        // fetchTasks() logic
        viewModelScope.launch {
            while (isActive) {
                if (networkMonitor.isConnected.value) {
                    retryFetchUntilSuccess()
                }

                delay(5.minutes)
            }
        }
    }

    private suspend fun retryFetchUntilSuccess() {
        while (true) {
            val result = repository.fetchTasks()
            if (result.isSuccess) return
            delay(10000) // wait 10 seconds before retry
        }
    }

    override fun onCleared() {
        super.onCleared()
        networkMonitor.stopMonitoring()
    }
}
