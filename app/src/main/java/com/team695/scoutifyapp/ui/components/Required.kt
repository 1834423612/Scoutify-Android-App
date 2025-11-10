package com.team695.scoutifyapp.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Required(
    var valid: (()-> Boolean)
) {
    var focusLeftYet by mutableStateOf(false)
    var focusStarted by mutableStateOf(false)
}