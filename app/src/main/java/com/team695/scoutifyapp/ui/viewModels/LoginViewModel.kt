package com.team695.scoutifyapp.ui.viewModels

import android.net.Uri
import android.util.Base64
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team695.scoutifyapp.BuildConfig
import com.team695.scoutifyapp.data.api.client.ScoutifyClient
import com.team695.scoutifyapp.data.api.model.User
import com.team695.scoutifyapp.data.api.service.TokenResponse
import com.team695.scoutifyapp.data.repository.UserRepository
import com.team695.scoutifyapp.ui.screens.login.LoginScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
        val loginUrl = "${BuildConfig.CASDOOR_ENDPOINT}/login/oauth/authorize".toUri()
            .buildUpon()
            .appendQueryParameter("client_id", BuildConfig.CASDOOR_CLIENT_ID)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("scope", "profile+email+openid")
            .appendQueryParameter("state", BuildConfig.CASDOOR_APP_NAME)
            .appendQueryParameter("code_challenge_method", "S256")
            .appendQueryParameter("code_challenge", challenge)
            .appendQueryParameter("redirect_uri", BuildConfig.CASDOOR_REDIRECT_URI)
            .build()
            .toString()


        _loginState.value = LoginStatus(
            verifier = verifier,
            loginUrl = loginUrl
        )

        return loginUrl
    }

    suspend fun tokenExchange(code: String) {
        try {
            if (loginState.value.verifier != null) {
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
            } else {
                _loginState.update {
                    it.copy(
                        error = LoginError.VERIFIER
                    )
                }
            }

        } catch (e: Exception) {
            _loginState.update {
                it.copy(
                    error = LoginError.TOKEN_EXCHANGE
                )
            }

            println("Error in casdoor token exchange: ${e.message}")
        }
    }

    suspend fun getUserInfo() {
        val res: Boolean = repository.getUserInfo()

        if (!res) {
            _loginState.value = LoginStatus(error = LoginError.ANDROID_ID)
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            _loginState.value = LoginStatus()

            repository.logout()
        }
    }
}

data class LoginStatus(
    val verifier: String? = null,
    val acToken: String? = null,
    val displayName: String? = null,
    val loginUrl: String? = null,
    val error: LoginError? = null,
)

enum class LoginError(val message: String) {
    VERIFIER("No verifier found"),
    TOKEN_EXCHANGE("Token exchange error"),
    ANDROID_ID("User AndroidID doesn't match device")
}

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