package com.team695.scoutifyapp.data.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.team695.scoutifyapp.data.api.service.NetworkService
import com.team695.scoutifyapp.data.repository.CommentRepository
import com.team695.scoutifyapp.data.repository.GameDetailRepository
import com.team695.scoutifyapp.data.repository.MatchRepository
import com.team695.scoutifyapp.data.repository.Repository
import com.team695.scoutifyapp.data.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


// IMPLEMENTATION OF NetworkService
// Please keep in mind that the NetworkMonitor MUST be actively monitoring
// by calling NetworkMonitor.startMonitoring() in order for NetworkMonitor.isConnected
// to return the correct network status
class NetworkMonitor(
    private val context: Context,
    private val taskRepository: TaskRepository,
    private val matchRepository: MatchRepository,
    private val gameDetailRepository: GameDetailRepository,
    private val commentRepository: CommentRepository
) : NetworkService {
    lateinit var repoList: List<Repository>
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager
    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected

    // listener for when network has changed
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(
            network: Network,
            capabilities: NetworkCapabilities
        ) {
            val validated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            _isConnected.value = validated
        }

        // lost connection to network
        override fun onLost(network: Network) {
            _isConnected.value = false
        }
    }

    init {
        repoList = listOf(
            matchRepository,
            taskRepository,
            commentRepository
        )

        startMonitoring()
    }

    override fun startMonitoring() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        // register the listener
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun stopMonitoring() {
        // unregister the listener
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    override suspend fun networkSync() {
        coroutineScope {
            this.launch(Dispatchers.IO) {
                while (isActive) {
                    gameDetailRepository.fetch()
                    gameDetailRepository.isReady.first { it }
                    retryFetchUntilSuccess()
                    retryPushUntilSuccess()
                    delay(5.minutes)
                }
            }
        }
    }

    private suspend fun retryFetchUntilSuccess() {
        withContext(Dispatchers.IO) {

            val fetches: MutableList<Repository> = mutableListOf()

            repoList.forEach {
                if (it.fetch().isFailure) {
                    fetches.add(it)
                }
            }

            var duration = 10.seconds
            while (fetches.isNotEmpty()) {
                isConnected.first { it }

                delay(duration)

                fetches.forEach {
                    if (it.fetch().isSuccess) {
                        fetches.remove(it)
                    }
                }

                duration = (duration + 10.seconds).coerceAtMost(40.seconds)
            }

            Log.d("HOME", "Fetched data successfully!")
        }
    }

    suspend fun retryPushUntilSuccess() {
        withContext(Dispatchers.IO) {
            val fetches: MutableList<Repository> = mutableListOf()

            repoList.forEach {
                if (it.push().isFailure) {
                    fetches.add(it)
                }
            }

            var duration = 10.seconds
            while (fetches.isNotEmpty()) {
                isConnected.first { it }

                delay(duration)

                fetches.forEach {
                    if (it.push().isSuccess) {
                        fetches.remove(it)
                    }
                }

                duration = (duration + 10.seconds).coerceAtMost(40.seconds)
            }

            Log.d("HOME", "Pushed data successfully!")
        }
    }
}