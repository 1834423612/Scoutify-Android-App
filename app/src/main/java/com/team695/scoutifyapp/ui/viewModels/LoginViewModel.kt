package com.team695.scoutifyapp.ui.viewModels

import android.util.Base64
import androidx.compose.runtime.currentRecomposeScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team695.scoutifyapp.BuildConfig
import com.team695.scoutifyapp.data.api.ScoutifyClient
import com.team695.scoutifyapp.data.api.model.LoginBody
import com.team695.scoutifyapp.data.api.service.LoginService
import com.team695.scoutifyapp.data.api.service.TokenResponse
import com.team695.scoutifyapp.data.api.service.UserInfoResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.security.MessageDigest
import java.security.SecureRandom

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

        _loginState.value = LoginStatus(
                verifier = verifier,
                loginUrl = loginUrl
            )

        return loginUrl
    }

    suspend fun tokenExchange(code: String) {
        try {
            val tokenRes: TokenResponse = service.getAccessToken(
                clientSecret = BuildConfig.CASDOOR_CLIENT_SECRET,
                clientId = BuildConfig.CASDOOR_CLIENT_ID,
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

    suspend fun getUserInfo(): UserInfoResponse {
        if (loginState.value.acToken != null) {
            val userInfo: UserInfoResponse = service.getUserInfo(
                authHeader = "Bearer ${_loginState.value.acToken}"
            )

            _loginState.update {
                it.copy(
                    loginUrl = null
                )
            }

            return userInfo
        } else {
            throw Exception("Access token is null; can't get casdoor user info")
        }
    }

    fun logout() {
        _loginState.value = LoginStatus()
    }
}