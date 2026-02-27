package com.team695.scoutifyapp.ui.screens.data

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun TeleopLockScreen(isOverlayActive: Boolean) {

    //disables back press
    BackHandler(enabled = isOverlayActive) {
    }


    val screenEdgePositionProvider = remember {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,      // The bounds of the parent component
                windowSize: IntSize,        // The bounds of the entire screen/window
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize   // The size of your grey box
            ): IntOffset {
                // x = 0 forces it to the absolute left edge of the screen
                val x = 0

                // y centers it vertically based on the full window height
                val y = (windowSize.height - popupContentSize.height) / 2

                return IntOffset(x, y)
            }
        }
    }

    if (isOverlayActive) {
        Popup (
            popupPositionProvider = screenEdgePositionProvider,
            properties = PopupProperties(
                focusable = false,
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(350.dp)
                    .background(Color.DarkGray.copy(alpha = 0.8f))
                    .pointerInput(Unit) {
                        detectTapGestures { }
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Navigation Locked",
                        tint = Color.White
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Teleop is running",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}