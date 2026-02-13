package com.team695.scoutifyapp.ui.viewModels

import android.util.Base64
import androidx.compose.runtime.currentRecomposeScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team695.scoutifyapp.BuildConfig
import com.team695.scoutifyapp.data.api.model.LoginBody
import com.team695.scoutifyapp.data.api.service.LoginService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.security.SecureRandom

data class LoginStatus(
    val verifier: String? = null,
    val error: String? = null
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

class LoginViewModel(private val service: LoginService): ViewModel() {
    private val _loginState = MutableStateFlow(LoginStatus())
    val loginState: StateFlow<LoginStatus> = _loginState

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

        _loginState.update {
            it.copy(
                verifier = verifier
            )
        }

        return loginUrl
    }
}