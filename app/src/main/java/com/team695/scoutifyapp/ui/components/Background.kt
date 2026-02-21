package com.team695.scoutifyapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.team695.scoutifyapp.R
import com.team695.scoutifyapp.ui.theme.PaneColor

@Composable
fun ImageBackground(x: Float, y: Float) {

    /*
    val fadeBrush = Brush.horizontalGradient(
        0.0f to Color(0x99000000),   // Start fully visible
        0.6f to Color(0x99000000),
        1.0f to Color.Transparent // Fade to transparent at the 100% mark
    )

    Box(
        modifier = Modifier.wrapContentWidth(unbounded = true)
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "background",
            modifier = Modifier
                .width(1500.dp)
                .graphicsLayer {
                    translationX = x
                    translationY = y
                }
                .drawWithContent {
                    // 3. Draw the actual image first
                    drawContent()

                    // 4. Draw the gradient over it with DstIn blend mode
                    drawRect(
                        brush = fadeBrush,
                        blendMode = BlendMode.DstIn
                    )
                },

            contentScale = ContentScale.Crop
        )
    }

     */
}

@Composable
fun BackgroundGradient() {
    /*
        Box(
            modifier = Modifier
                .wrapContentWidth(unbounded = true)
                .fillMaxSize()
                .width(2000.dp)
                .background(Brush.verticalGradient(
                    colors = listOf(
                        PaneColor,
                        PaneColor.copy(alpha=0.8f)
                    )
                ))

        ) {
        }
        
     */
}
