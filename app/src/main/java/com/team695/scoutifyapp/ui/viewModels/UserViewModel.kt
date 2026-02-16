package com.team695.scoutifyapp.ui.viewModels

import androidx.lifecycle.ViewModel
import com.team695.scoutifyapp.data.api.model.User
import com.team695.scoutifyapp.data.api.service.UserInfoResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel(): ViewModel() {
    private val _userState = MutableStateFlow(User())
    val userState: StateFlow<User> = _userState

    fun updateUser(user: UserInfoResponse) {
        _userState.value = User(
            name = user.name,
            preferredUsername = user.preferredUsername,
            picture = user.picture,
            email = user.email,
        )
    }
}