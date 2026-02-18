package com.team695.scoutifyapp.data.api.service

import kotlinx.coroutines.flow.StateFlow

interface NetworkService {
    val isConnected: StateFlow<Boolean> // use stateflow for live updates
    fun startMonitoring()
    fun stopMonitoring()
}