package com.team695.scoutifyapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team695.scoutifyapp.data.api.service.MatchService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val service: MatchService): ViewModel() {
    private val _matches = MutableStateFlow<String>("")
    val matches: StateFlow<String?> = _matches

    fun fetchMatches() {
        viewModelScope.launch {
            try {
                val matchesRes = service.listMatches()
                _matches.value = matchesRes.string()
            } catch (e: Exception) {
                println("Error fetching matches: ${e.message}")
            }
        }
    }
}