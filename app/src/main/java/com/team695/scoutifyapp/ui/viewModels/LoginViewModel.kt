package com.team695.scoutifyapp.ui.viewModels

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team695.scoutifyapp.BuildConfig
import com.team695.scoutifyapp.data.api.client.ScoutifyClient
import com.team695.scoutifyapp.data.api.model.User
import com.team695.scoutifyapp.data.api.service.TokenResponse
import com.team695.scoutifyapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.security.SecureRandom

class LoginViewModel(private val repository: UserRepository): ViewModel() {
    private val _loginState = MutableStateFlow(LoginStatus())
    val loginState: StateFlow<LoginStatus> = _loginState

    private val _userState = repository.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    val userState: StateFlow<User?> = _userState

    init {
        viewModelScope.launch {
            val token: String? = ScoutifyClient.tokenManager.getToken()

            _loginState.value = LoginStatus(
                acToken = token?.ifEmpty { null }
            )
        }
    }
    fun generateLoginURL(): String {
        val verifier = generateCodeVerifier()
        val challenge = generateCodeChallenge(verifier)

        val loginUrl = "${BuildConfig.CASDOOR_ENDPOINT}/login/oauth/authorize?" +
                "client_id=${BuildConfig.CASDOOR_CLIENT_ID}" +
                "&response_type=code" +
                "&scope=profile email openid" +
                "&state=${BuildConfig.CASDOOR_APP_NAME}" +
                "&code_challenge_method=S256" +
                "&code_challenge=$challenge" +
                "&redirect_uri=${BuildConfig.CASDOOR_REDIRECT_URI}"

        _loginState.value = LoginStatus(
            verifier = verifier,
            loginUrl = loginUrl
        )

        return loginUrl
    }

    suspend fun tokenExchange(code: String) {
        try {
            val tokenRes: TokenResponse = repository.getAccessToken(
                code = code,
                verifier = loginState.value.verifier!!
            )

            _loginState.update {
                it.copy(
                    acToken = tokenRes.accessToken
                )
            }

            ScoutifyClient.tokenManager.saveToken(tokenRes.accessToken)

        } catch (e: Exception) {
            _loginState.update {
                it.copy(
                    error = e.message
                )
            }

            println("Error in casdoor token exchange: ${e.message}")
        }
    }

    suspend fun getUserInfo() {
        repository.getUserInfo()
    }

    suspend fun logout() {
        ScoutifyClient.tokenManager.saveToken("")
        _loginState.value = LoginStatus()
    }
}

data class LoginStatus(
    val verifier: String? = null,
    val error: String? = null,
    val acToken: String? = null,
    val loginUrl: String? = null,
)

fun generateCodeVerifier(): String {
    val sr = SecureRandom()
    val code = ByteArray(32)
    sr.nextBytes(code)
    return Base64.encodeToString(code, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
}

fun generateCodeChallenge(verifier: String): String {
    val bytes = verifier.toByteArray(Charsets.US_ASCII)
    val md = MessageDigest.getInstance("SHA-256")
    md.update(bytes, 0, bytes.size)
    val digest = md.digest()
    return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
}