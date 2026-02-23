package com.team695.scoutifyapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team695.scoutifyapp.data.api.NetworkMonitor
import com.team695.scoutifyapp.data.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.minutes


// every 5 minutes checks that user is connected to wifi
// then calls fetchTasks() every 10 seconds until .isSuccessful
class TasksViewModel(
    private val networkMonitor: NetworkMonitor,
    private val repository: TaskRepository
) : ViewModel() {

    init {
        /*
        start a coroutine which will perform the
         fetchTasks() logic
         */

        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                if (networkMonitor.isConnected.value) {
                    retryFetchUntilSuccess()
                }

                delay(5.minutes)
            }
        }
    }

    private suspend fun retryFetchUntilSuccess() {
        withContext(Dispatchers.IO) {
            var result = repository.fetchTasks()

            while (result.isFailure) {
                delay(10*1000)
                result = repository.fetchTasks()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        networkMonitor.stopMonitoring()
    }
}
