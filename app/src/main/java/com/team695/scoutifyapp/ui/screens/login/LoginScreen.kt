package com.team695.scoutifyapp.ui.screens.login

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import com.team695.scoutifyapp.data.api.CasdoorClient
import com.team695.scoutifyapp.data.api.ScoutifyClient
import com.team695.scoutifyapp.data.api.service.LoginService
import com.team695.scoutifyapp.ui.viewModels.LoginViewModel
import com.team695.scoutifyapp.ui.viewModels.ViewModelFactory
import com.team695.scoutifyapp.ui.extensions.deviceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.security.SecureRandom

private const val TAG = "CasdoorLogin"

@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
) {

    //println("DEVICE ID: ${LocalContext.current.deviceId}")
    val loginState by loginViewModel.loginState.collectAsState()
    val userInfo by loginViewModel.userState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    if (loginState.acToken == null) {
        if (loginState.loginUrl != null) {
            CasdoorWebView(
                url = loginState.loginUrl!!,
                onCodeReceived = { code ->
                    Log.d(TAG, "🚀 Auth Code Received: $code")

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            loginViewModel.tokenExchange(code)
                            Log.d(TAG, "✅ Token Received: ${loginState.acToken}")

                           loginViewModel.getUserInfo()
                        } catch (e: Exception) {
                            Log.e(TAG, "❌ Login Error", e)
                        }
                    }
                },
                onNavigationBack = { }
            )
        } else {
            Button(onClick = {
                loginViewModel.generateLoginURL()
            }) {
                Text(text = "Login with Casdoor")
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (loginState.acToken != null) {
                if (userInfo?.name != null) {
                    Text(text = "Welcome, ${userInfo?.name}!", color = Color.Green)
                    Text(text = "Logged in successfully", color = Color.Green)

                    Button(onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            loginViewModel.logout()
                        }
                    }) {
                        Text(text = "Log out")
                    }

                    LaunchedEffect(loginState.verifier) {
                        delay(3000)
                        println("TOKEN: ${ScoutifyClient.tokenManager.getToken()}")
                        navController.navigate("home")
                    }
                }

            } else {
                Button(onClick = {
                    loginViewModel.generateLoginURL()
                }) {
                    Text(text = "Login with Casdoor")
                }
            }
        }
    }
}
@Composable
fun CasdoorWebView(
    url: String,
    onCodeReceived: (String) -> Unit,
    onNavigationBack: () -> Unit
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.userAgentString = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.5672.136 Mobile Safari/537.36"

                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        Log.d(TAG, "⚡ Page Started: $url")
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val uri = request?.url
                        if (uri?.scheme == "casdoor") {
                            val code: String? = uri.getQueryParameter("code")
                            if (code != null) {
                                onCodeReceived(code)
                                return true
                            }
                        }
                        return super.shouldOverrideUrlLoading(view, request)
                    }

                    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                        Log.e(TAG, "⛔ WebView Error: ${error?.description}")
                    }
                }
                loadUrl(url)
            }
        },
        update = { webView ->
            if (webView.url != url && webView.originalUrl != url) {
                webView.loadUrl(url)
            }
        }
    )
}