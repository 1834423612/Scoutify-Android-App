package com.team695.scoutifyapp.data.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.team695.scoutifyapp.data.api.service.NetworkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


// IMPLEMENTATION OF NetworkService
// Please keep in mind that the NetworkMonitor MUST be actively monitoring
// by calling NetworkMonitor.startMonitoring() in order for NetworkMonitor.isConnected
// to return the correct network status
class NetworkMonitor(private val context: Context) : NetworkService {
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
}