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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.security.SecureRandom

private const val TAG = "CasdoorLogin"

@Composable
fun LoginScreen() {
    // --- CONFIGURATION ---
    val endpoint = ""
    val clientID = ""
    val clientSecret = ""
    val redirectUri = ""
    val appName = ""

    // --- STATE ---
    var isLogin by rememberSaveable { mutableStateOf(false) }
    var acToken by rememberSaveable { mutableStateOf("") }
    var loginUrl by rememberSaveable { mutableStateOf<String?>(null) }
    var userName by rememberSaveable { mutableStateOf("") }
    var userAvatar by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }

    // PKCE State
    var codeVerifier by rememberSaveable { mutableStateOf("") }

    if (loginUrl != null) {
        CasdoorWebView(
            url = loginUrl!!,
            onCodeReceived = { code ->
                isLoading = true
                errorMessage = ""
                userName = "Fetching Profile..."
                Log.d(TAG, "🚀 Auth Code Received: $code")

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // 1. MANUAL TOKEN EXCHANGE
                        val token = manualTokenExchange(
                            endpoint = endpoint,
                            code = code,
                            verifier = codeVerifier,
                            clientId = clientID,
                            clientSecret = clientSecret
                        )
                        Log.d(TAG, "✅ Token Received: $token")
                        acToken = token

                        // 2. MANUAL USER INFO FETCH (The Fix)
                        val userInfo = manualGetUserInfo(endpoint, token)

                        withContext(Dispatchers.Main) {
                            userName = userInfo.optString("name", "Unknown")
                            if (userName.isEmpty()) userName = userInfo.optString("preferred_username", "User")

                            userAvatar = userInfo.optString("picture", "")

                            isLogin = true
                            isLoading = false
                            loginUrl = null
                            Log.d(TAG, "✅ Login Complete. User: $userName")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ Login Error", e)
                        withContext(Dispatchers.Main) {
                            isLoading = false
                            errorMessage = "Error: ${e.message}"
                            loginUrl = null
                        }
                    }
                }
            },
            onNavigationBack = { loginUrl = null }
        )
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLogin) {
                Text(text = "Welcome, $userName!")
                Text(text = "Logged in successfully", color = Color.Green)
            }
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red)
            }

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(onClick = {
                    if (!isLogin) {
                        // 1. Generate PKCE
                        val verifier = generateCodeVerifier()
                        codeVerifier = verifier
                        val challenge = generateCodeChallenge(verifier)

                        // 2. Build URL
                        val authUrl = "$endpoint/login/oauth/authorize?" +
                                "client_id=$clientID" +
                                "&response_type=code" +
                                "&scope=profile email openid" + // Added 'email' and 'openid' for better data
                                "&state=$appName" +
                                "&code_challenge_method=S256" +
                                "&code_challenge=$challenge" +
                                "&redirect_uri=$redirectUri"

                        Log.d(TAG, "🆕 Login URL: $authUrl")
                        loginUrl = authUrl
                    } else {
                        // Logout
                        isLogin = false
                        userName = ""
                        acToken = ""
                    }
                }) {
                    Text(text = if (isLogin) "Logout" else "Login with Casdoor")
                }
            }
        }
    }
}

// --- HELPER FUNCTIONS ---

suspend fun manualGetUserInfo(endpoint: String, accessToken: String): JSONObject {
    return withContext(Dispatchers.IO) {
        // We use the OIDC standard endpoint '/api/userinfo' which usually returns the cleanest JSON
        val url = URL("$endpoint/api/userinfo")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.setRequestProperty("Authorization", "Bearer $accessToken")

        val responseCode = conn.responseCode
        if (responseCode == 200) {
            val response = conn.inputStream.bufferedReader().use { it.readText() }
            Log.d(TAG, "👤 User Info JSON: $response") // Log the raw JSON to see what we got
            return@withContext JSONObject(response)
        } else {
            throw Exception("Failed to fetch user info: HTTP $responseCode")
        }
    }
}

suspend fun manualTokenExchange(
    endpoint: String,
    code: String,
    verifier: String,
    clientId: String,
    clientSecret: String
): String {
    return withContext(Dispatchers.IO) {
        val url = URL("$endpoint/api/login/oauth/access_token")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        conn.setRequestProperty("Accept", "application/json")

        val postData = "grant_type=authorization_code" +
                "&client_id=$clientId" +
                "&client_secret=$clientSecret" +
                "&code=$code" +
                "&code_verifier=$verifier"

        conn.outputStream.use { it.write(postData.toByteArray()) }

        val responseCode = conn.responseCode
        if (responseCode == 200) {
            val response = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(response)
            if (json.has("access_token")) {
                return@withContext json.getString("access_token")
            } else {
                throw Exception("No access_token in response: $response")
            }
        } else {
            val errorStream = conn.errorStream?.bufferedReader()?.use { it.readText() }
            throw Exception("Server Error $responseCode: $errorStream")
        }
    }
}

// ... (Keep generateCodeVerifier, generateCodeChallenge, and CasdoorWebView the same as before)
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