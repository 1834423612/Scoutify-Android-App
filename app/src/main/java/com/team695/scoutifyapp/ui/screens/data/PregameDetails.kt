package com.team695.scoutifyapp.ui.screens.data

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
//import androidx.compose.ui.graphics.drawscope.drawImage
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team695.scoutifyapp.R
import com.team695.scoutifyapp.data.types.GameFormState
import com.team695.scoutifyapp.ui.viewModels.DataViewModel
import com.team695.scoutifyapp.ui.viewModels.PregameViewModel

//@Composable
//fun PregameDetails(
//    dataViewModel: DataViewModel,
//    formState: GameFormState,
//    viewModel: PregameViewModel
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize()
//        ) {
//
//            Text(
//                text = "Pregame",
//                color = Color.White,
//                fontSize = 24.sp
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Row(
//                modifier = Modifier.fillMaxSize(),
//                horizontalArrangement = Arrangement.spacedBy(24.dp)
//            ) {
//
//                // Left Pane (placeholder)
//                Column(
//                    modifier = Modifier
//                        .weight(1f)
//                        .fillMaxHeight()
//                ) {
//                    Text("Scouting Controls", color = Color.White)
//                }
//
//                // Right Pane - Field
//                Box(
//                    modifier = Modifier
//                        .weight(2f)
//                        .fillMaxHeight()
//                        .clip(RectangleShape)
//                        .background(Color(0xFF2A2A2A)),
//                    contentAlignment = Alignment.Center
//                ) {
//                    val fieldImage =
//                        ImageBitmap.imageResource(id = R.drawable.map)
//
//                    val robotImage =
//                        ImageBitmap.imageResource(id = R.drawable.robot)
//
//                    FieldCanvas(
//                        viewModel = viewModel,
//                        fieldImage = fieldImage,
//                        robotImage = robotImage
//                    )
//                }
//            }
//        }
//    }
//}

@Composable
fun FieldCanvas(
    viewModel: PregameViewModel,
    fieldImage: ImageBitmap,
    robotImage: ImageBitmap
) {

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .border(2.dp, Color.Gray)
            .clip(RectangleShape)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    // Convert tap Y into normalized 0f–1f
                    viewModel.position = (offset.y.toDouble() / size.height)
                        .coerceIn(0.0, 1.0)
                }
            }
    ) {

        // Draw field stretched to canvas
        drawImage(
            image = fieldImage,
            dstSize = IntSize(size.width.toInt(), size.height.toInt())
        )

        // Draw robot at tapped position
        viewModel.position?.let { tapOffset ->

            val robotSize = 80f

            // Convert normalized Y (0–1) to pixel position
            val yPx = viewModel.position * size.height

            drawImage(
                image = robotImage,
                dstOffset = IntOffset(
                    x = (size.width / 2f - robotSize / 2f).toInt(),
                    y = (yPx - robotSize / 2f).toInt()
                ),
                dstSize = IntSize(robotSize.toInt(), robotSize.toInt())
            )
        }
    }
}



//package com.team695.scoutifyapp.ui.screens.data
//
//import androidx.compose.animation.animateColorAsState
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.*
//import androidx.compose.foundation.gestures.detectDragGestures
//import androidx.compose.foundation.gestures.detectTapGestures
//import androidx.compose.foundation.gestures.snapping.SnapPosition.Center.position
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.selection.toggleable
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.geometry.CornerRadius
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.ImageBitmap
//import androidx.compose.ui.graphics.Path
//import androidx.compose.ui.graphics.RectangleShape
//import androidx.compose.ui.graphics.lerp
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.res.imageResource
//import androidx.compose.ui.semantics.Role
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.IntSize
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.team695.scoutifyapp.R
//import com.team695.scoutifyapp.data.types.ENDGAME_END_TIME
//import com.team695.scoutifyapp.data.types.GameFormState
//import com.team695.scoutifyapp.data.types.SHIFT1_END_TIME
//import com.team695.scoutifyapp.data.types.SHIFT4_END_TIME
//import com.team695.scoutifyapp.data.types.TELEOP_TIME_THRESHOLD
//import com.team695.scoutifyapp.data.types.TRANSITION_END_TIME
//import com.team695.scoutifyapp.data.types.TeleopSection
//import com.team695.scoutifyapp.ui.components.NullableCheckbox
//import com.team695.scoutifyapp.ui.components.buttonHighlight
//import com.team695.scoutifyapp.ui.theme.AccentGreen
//import com.team695.scoutifyapp.ui.theme.BlueAlliance
//import com.team695.scoutifyapp.ui.theme.Border
//import com.team695.scoutifyapp.ui.theme.DarkGunmetal
//import com.team695.scoutifyapp.ui.theme.DarkishGunmetal
//import com.team695.scoutifyapp.ui.theme.Gunmetal
//import com.team695.scoutifyapp.ui.theme.LightGunmetal
//import com.team695.scoutifyapp.ui.theme.RedAlliance
//import com.team695.scoutifyapp.ui.theme.TextPrimary
//import com.team695.scoutifyapp.ui.theme.mediumCornerRadius
//import com.team695.scoutifyapp.ui.theme.smallCornerRadius
//import com.team695.scoutifyapp.ui.viewModels.DataViewModel
//import com.team695.scoutifyapp.ui.viewModels.PregameViewModel
//import com.team695.scoutifyapp.ui.viewModels.Stroke
//import kotlinx.coroutines.delay
//import kotlin.math.abs
//
@Composable
fun PregameDetails(
    dataViewModel: DataViewModel,
    formState: GameFormState,
    viewModel: PregameViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {

            TopbarNoButton(
                title = "Pregame",
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // LEFT PANE: Scouting Controls
                Column(
                    modifier = Modifier
                        .weight(1f) // Takes up 1/3 of the screen width
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    ScoutingBubble(
                        modifier = Modifier
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = "Match Status",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // UX Pattern: Whole row is clickable for faster, error-free tapping
                        CheckboxRow (
                            label = "Robot is on field",
                            isChecked = formState.gameDetails.robotOnField,
                            onCheckedChange = { isChecked: Boolean? ->
                                val nextState: Boolean = when (isChecked) {
                                    null -> false
                                    true -> false
                                    false -> true
                                }

                                dataViewModel.formEvent(
                                    gameDetails = formState.gameDetails.copy(
                                        robotOnField = nextState
                                    )
                                )
                            }
                        )

                        CheckboxRow(
                            label = "Robot is preloaded",
                            isChecked = formState.gameDetails.robotPreloaded,
                            onCheckedChange = { isChecked: Boolean? ->
                                val nextState: Boolean = when (isChecked) {
                                    null -> false
                                    true -> false
                                    false -> true
                                }

                                dataViewModel.formEvent(
                                    gameDetails = formState.gameDetails.copy(
                                        robotPreloaded = nextState
                                    )
                                )
                            }
                        )

                    }
                }

                // RIGHT PANE: Field Canvas
                ScoutingBubble(
                    modifier = Modifier
                        .weight(2f) // Takes up 2/3 of the screen width
                        .fillMaxHeight()
                ) {
                    Text(
                        text = "Field Map",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Canvas wrapper
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF2A2A2A)), // Dark gray placeholder
                        contentAlignment = Alignment.Center
                    ) {
                        var fieldImage = ImageBitmap.imageResource(id = R.drawable.map)
                        var robot=ImageBitmap.imageResource(id = R.drawable.robot)
                        if(formState.alliance == "B"){
                            fieldImage=ImageBitmap.imageResource(id = R.drawable.blue_map)
                            robot=ImageBitmap.imageResource(id = R.drawable.robot__1_)
                        }
                        FieldCanvas(viewModel, fieldImage,robot)
                    }
                }
            }
        }
    }
}