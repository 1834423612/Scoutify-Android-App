package com.team695.scoutifyapp.ui.screens.login

import android.graphics.Bitmap
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.team695.scoutifyapp.data.api.client.ScoutifyClient
import com.team695.scoutifyapp.ui.theme.Gunmetal
import com.team695.scoutifyapp.ui.theme.LightGunmetal
import com.team695.scoutifyapp.ui.theme.TextPrimary
import com.team695.scoutifyapp.ui.theme.smallCornerRadius
import com.team695.scoutifyapp.ui.theme.Border // Import Border color
import com.team695.scoutifyapp.ui.theme.BorderSecondary
import com.team695.scoutifyapp.ui.theme.DarkGunmetal
import com.team695.scoutifyapp.ui.theme.mediumCornerRadius
import com.team695.scoutifyapp.ui.viewModels.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "CasdoorLogin"

@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
) {

    //println("DEVICE ID: ${LocalContext.current.androidID}")
    val loginState by loginViewModel.loginState.collectAsState()
    val userInfo by loginViewModel.userState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    if (loginState.acToken == null) {
        if (loginState.loginUrl != null) {
            CasdoorWebView(
                url = loginState.loginUrl!!,
                onCodeReceived = { code ->

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            loginViewModel.tokenExchange(code)

                            loginViewModel.getUserInfo()
                        } catch (e: Exception) {
                            Log.e(TAG, "Login error", e)
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
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(mediumCornerRadius))
                        .border(
                            1.dp,
                            Border,
                            RoundedCornerShape(mediumCornerRadius)
                        )
                        .background(DarkGunmetal)
                ) {
                    Button(
                        onClick = {
                            loginViewModel.generateLoginURL()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text(text = "Sign in with Casdoor", color = TextPrimary)
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Welcome, ${userInfo?.name}!",
                    color = TextPrimary
                )
                Text(
                    text = "Logged in successfully",
                    color = TextPrimary
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(mediumCornerRadius))
                        .border(
                            1.dp,
                            Border,
                            RoundedCornerShape(mediumCornerRadius)
                        )
                        .background(DarkGunmetal)
                ) {
                    Button(
                        onClick = {
                            loginViewModel.logout()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text(text = "Log out", color = TextPrimary)
                    }
                }
            }

            LaunchedEffect(loginState.verifier) {
                delay(3000)
                navController.navigate("home")
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

                /*
                This force dark mode doesn't work for some reason

                if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                    WebSettingsCompat.setAlgorithmicDarkeningAllowed(settings, true)
                }
                */

                settings.userAgentString =
                    "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.5672.136 Mobile Safari/537.36"

                webViewClient = object : WebViewClient() {
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

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        Log.e(TAG, "Webview error: ${error?.description}")
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