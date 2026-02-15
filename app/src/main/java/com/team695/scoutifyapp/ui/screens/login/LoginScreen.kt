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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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

    val loginState by loginViewModel.loginState.collectAsState()
    var username by remember { mutableStateOf("") }

    // Show error dialog when there's an error
    if (loginState.error != null) {
        AlertDialog(
            onDismissRequest = { loginViewModel.clearError() },
            title = { Text("Login Error") },
            text = { Text(loginState.error!!) },
            confirmButton = {
                TextButton(onClick = { loginViewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }

    // Handle navigation only when explicitly ready
    LaunchedEffect(loginState.navigationReady) {
        if (loginState.navigationReady) {
            try {
                navController.navigate("home") {
                    // Clear the login screen from back stack
                    popUpTo("login") { inclusive = true }
                }
                loginViewModel.resetNavigation()
            } catch (e: Exception) {
                Log.e(TAG, "Navigation error: ${e.message}", e)
                loginViewModel.setNavigationError("Failed to navigate to home screen")
            }
        }
    }

    // Fetch user info when token is available but user info hasn't been fetched yet
    LaunchedEffect(loginState.acToken, loginState.userInfo) {
        if (loginState.acToken != null && 
            loginState.userInfo == null && 
            !loginState.isLoading && 
            loginState.error == null) {
            val userInfo = loginViewModel.getUserInfo()
            if (userInfo != null) {
                username = userInfo.name ?: "Unknown User"
                Log.d(TAG, "✅ Login Complete. User: $username")
            }
            // Error handling is done by the ViewModel, which sets the error state
        }
    }

    if (loginState.loginUrl != null) {
        CasdoorWebView(
            url = loginState.loginUrl!!,
            onCodeReceived = { code ->
                Log.d(TAG, "🚀 Auth Code Received: $code")

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // 1. MANUAL TOKEN EXCHANGE
                        loginViewModel.tokenExchange(code)
                        Log.d(TAG, "✅ Token Received: ${loginState.acToken}")

                        // 2. MANUAL USER INFO FETCH (The Fix)
                        val userInfo = loginViewModel.getUserInfo()

                        withContext(Dispatchers.Main) {
                            username = userInfo.name ?: "Unknown User"
                            Log.d(TAG, "✅ Login Complete. User: $username")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ Login Error", e)
                    }
                }
            },
            onNavigationBack = { }
        )
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (loginState.isLoading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Logging in...", style = MaterialTheme.typography.bodyMedium)
            } else if (loginState.acToken != null) {
                val displayName = loginState.userInfo?.name ?: username.ifEmpty { "User" }
                Text(text = "Welcome, $displayName!", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Logged in successfully", color = Color.Green)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Navigating to home...", style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(text = "Scoutify Login", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    if (loginState.verifier == null) {
                        // 1. Generate PKCE
                        loginViewModel.generateLoginURL()
                    } else {
                        // Logout
                        loginViewModel.logout()
                        username = ""
                    }
                }) {
                    Text(text = if (loginState.acToken != null) "Logout" else "Login with Casdoor")
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