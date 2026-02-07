package com.team695.scoutifyapp.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team695.scoutifyapp.data.api.model.LoginBody
import com.team695.scoutifyapp.data.api.service.LoginService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val service: LoginService): ViewModel() {
    private val _loginRes = MutableStateFlow<String>("")
    val loginRes: StateFlow<String?> = _loginRes

    fun login() {
        viewModelScope.launch {
            try {
                val body: LoginBody = LoginBody(
                    695,
                    "alex",
                    "87e31e75d0229aeac6909b2c12dd5feb43380238b011f985ee9e58bf8e4d40ee"
                )
                val res = service.login(body)
                _loginRes.value = res.string()
            } catch (e: Exception) {
                println("Error when trying to log in: ${e.message}")
            }
        }
    }
}