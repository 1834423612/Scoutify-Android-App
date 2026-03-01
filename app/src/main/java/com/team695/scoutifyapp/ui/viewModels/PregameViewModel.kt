package com.team695.scoutifyapp.ui.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel

class PregameViewModel : ViewModel() {
    var position by mutableStateOf(0.5)
}