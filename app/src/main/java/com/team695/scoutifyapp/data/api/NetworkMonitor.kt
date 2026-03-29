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
import com.team695.scoutifyapp.data.repository.TeamNameRepository
import com.team695.scoutifyapp.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

val FETCH_INTERVAL = 5.minutes
val RETRY_BASE_INTERVAL = 10.seconds

/*
  IMPLEMENTATION OF NetworkService
    Please keep in mind that the NetworkMonitor MUST be actively monitoring
    by calling NetworkMonitor.startMonitoring() in order for NetworkMonitor.isConnected
    to return the correct network status
 */
class NetworkMonitor(
    private val context: Context,
    private val taskRepository: TaskRepository,
    private val matchRepository: MatchRepository,
    private val gameDetailRepository: GameDetailRepository,
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
    private val teamNameRepository: TeamNameRepository
) : NetworkService {
    var repoList: List<Repository> = listOf(
        matchRepository,
        taskRepository,
        commentRepository,
        teamNameRepository
    )
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager
    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected
    private var isMonitoringRegistered = false

    // listener for when network has changed
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(
            network: Network,
            capabilities: NetworkCapabilities
        ) {
            val validated = capabilities.hasCapability(
                 NetworkCapabilities.NET_CAPABILITY_VALIDATED
            )

            _isConnected.value = validated
        }

        // lost connection to network
        override fun onLost(network: Network) {
            _isConnected.value = false
        }
    }

    init {
        startMonitoring()
    }

    override fun startMonitoring() {
        if (isMonitoringRegistered) return

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        // register the listener
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        isMonitoringRegistered = true
    }

    override fun stopMonitoring() {
        if (!isMonitoringRegistered) return

        // unregister the listener
        connectivityManager.unregisterNetworkCallback(networkCallback)
        isMonitoringRegistered = false
    }

    override suspend fun networkSync(maxErrors: Int) {
        withContext(Dispatchers.IO) {
            while (isActive) {
                userRepository.currentUser.first {
                    it != null && it.name != "WRONG_USER" && it.name != "LOADING"
                }

                retryFetchUntilSuccess(maxErrors)
                retryPushUntilSuccess(maxErrors)

                delay(FETCH_INTERVAL)
            }
        }
    }

    private suspend fun retryFetchUntilSuccess(maxErrors: Int) {
        withContext(Dispatchers.IO) {
            var errors: Int = 0
            var gameDetailsSuccess: Boolean = false

            // fetch game details first since the other repos are dependent on it
            while (!gameDetailsSuccess && errors < maxErrors) {
                isConnected.first { it }
                val result = gameDetailRepository.fetch()

                if (result.isSuccess) {
                    Log.d("Network_Monitor", "successfully fetched game details")
                    gameDetailsSuccess = true
                }
                else {
                    Log.d("Network_Monitor", "Error fetching game details, retrying...")
                    errors += 1
                    delay(RETRY_BASE_INTERVAL)
                }
            }

            if (gameDetailsSuccess) {
                Log.d("Network_Monitor", "Fetched game details successfully!")
            }
            else {
                Log.e("Network_Monitor", "Maximum errors limit exceeded for fetching game details: $maxErrors errors")
                return@withContext
            }

            // after game details fetched, fetch all other repositories.
            val fetchList: MutableList<Repository> = repoList.toMutableList()
            var duration = RETRY_BASE_INTERVAL
            errors = 0

            // delay(duration)

            while (fetchList.isNotEmpty() && errors < maxErrors) {
                isConnected.first { it }

                val iter = fetchList.iterator()
                while (iter.hasNext()) {
                    val repo = iter.next()
                    val result = repo.fetch()

                    if (result.isSuccess) {
                        iter.remove()
                    }
                    else {
                        errors += 1
                        val exception = result.exceptionOrNull()
                        Log.e("Network_Monitor", "Error fetching repo", exception)
                    }
                }

                if (fetchList.isNotEmpty() && errors < maxErrors) {
                    delay(duration)
                    duration = (duration + RETRY_BASE_INTERVAL).coerceAtMost(40.seconds)
                }
            }

            if (fetchList.isEmpty()) {
                Log.d("Network_Monitor", "Fetched all data successfully!")
            }
            else {
                Log.e("Network_Monitor", "Maximum errors limit exceeded for fetching repositories: $maxErrors errors")
            }
        }
    }

    suspend fun retryPushUntilSuccess(maxErrors: Int) {
        withContext(Dispatchers.IO) {
            var errors: Int = 0
            var gameDetailsSuccess: Boolean = false

            // push game details first
            while (!gameDetailsSuccess && errors < maxErrors) {
                isConnected.first { it }
                val result = gameDetailRepository.push()

                if (result.isSuccess) {
                    gameDetailsSuccess = true
                }
                else {
                    errors += 1
                    delay(RETRY_BASE_INTERVAL)
                }
            }

            if (gameDetailsSuccess) {
                Log.d("Network_Monitor", "Pushed game details successfully!")
            }
            else {
                Log.e("Network_Monitor", "Maximum errors limit exceeded for pushing game details: $maxErrors errors")
                return@withContext
            }

            // push all other repositories
            val pushList: MutableList<Repository> = repoList.toMutableList()
            var duration = RETRY_BASE_INTERVAL
            errors = 0

            while (pushList.isNotEmpty() && errors < maxErrors) {
                isConnected.first { it }

                val iter = pushList.iterator()
                while (iter.hasNext()) {
                    val repo = iter.next()
                    val result = repo.push()

                    if (result.isSuccess) {
                        iter.remove()
                    }
                    else {
                        errors += 1
                        val exception = result.exceptionOrNull()
                        Log.e("Network_Monitor", "Error pushing repo", exception)
                    }
                }

                if (pushList.isNotEmpty() && errors < maxErrors) {
                    delay(duration)
                    duration = (duration + RETRY_BASE_INTERVAL).coerceAtMost(40.seconds)
                }
            }

            if (pushList.isEmpty()) {
                Log.d("Network_Monitor", "Pushed all data successfully!")
            }
            else {
                Log.d("Network_Monitor", "Maximum errors limit exceeded for pushing repositories: $maxErrors errors")
            }
        }
    }
}

/*
 * Current status of NetworkMonitor Singleton
 * Holds current network job that can be restarted when user logs out
 */

object NetworkMonitorStatus {
    var currentNetworkJob: Job? = null
}
