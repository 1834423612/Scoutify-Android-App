package com.team695.scoutifyapp.ui.screens.home

import com.team695.scoutifyapp.data.api.model.Match
import com.team695.scoutifyapp.data.api.service.MatchService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel(private val service: MatchService): ViewModel() {
    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches

    fun fetchMatches() {
        viewModelScope.launch {
            try {
                val matchesRes = service.listMatches()
                _matches.value = matchesRes
            } catch (e: Exception) {
                println("Error fetching matches: ${e.message}")
            }
        }
    }
}