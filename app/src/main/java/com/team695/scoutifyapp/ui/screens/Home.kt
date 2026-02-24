package com.team695.scoutifyapp.ui.screens

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team695.scoutifyapp.ui.PenViewModel
import com.team695.scoutifyapp.ui.Stroke
import com.team695.scoutifyapp.ui.components.InfoCard
import com.team695.scoutifyapp.ui.components.Bison
import kotlin.io.path.moveTo


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(viewModel: PenViewModel = viewModel(), navigateToPitSchedule: () -> Unit, navigateToMatchSchedule:()->Unit) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            InfoCard(
                title = "Team 695",
                description = "Welcome to our scouting app!"
            )
            Bison()


            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = navigateToPitSchedule) {
                Text("Pit Scouting Assignments")
            }
            Button(onClick = navigateToMatchSchedule) {
                Text("Match Scouting Assignments ")
            }

            //auton path example
            Column {
                DrawCanvas(viewModel)
                Row {
                    Button(onClick = { viewModel.undo() }) {
                        Text("undo")
                    }
                    Button(onClick = { viewModel.redo() }) {
                        Text("redo")
                    }
                    Button(onClick = { viewModel.utensil = "path" }) {
                        Text("path")
                    }
                    Button(onClick = { viewModel.utensil = "shoot" }) {
                        Text("shoot")
                    }
                    Button(onClick = { viewModel.utensil = "intake" }) {
                        Text("intake")
                    }
                    Button(onClick = { viewModel.utensil = "broke" }) {
                        Text("broke")
                    }
                }
                Button(onClick = { viewModel.reset() }) {
                    Text("reset")
                }
            }
        }
    }
}


@Composable
fun DrawCanvas(
    viewModel: PenViewModel = viewModel()
) {
    val paths = viewModel.paths
    val current = viewModel.currentStroke

    Canvas(
        modifier = Modifier
            .width(300.dp)
            .height(150.dp)
            .border(2.dp, Color.Gray)
            .clip(RectangleShape)
            .pointerInput(viewModel.utensil) {
                detectTapGestures { offset ->
                    if (viewModel.utensil != "path") {
                        viewModel.addLabeledPoint(offset, viewModel.utensil)
                    }
                }
            }
            .pointerInput(viewModel.utensil,viewModel.lastDragPosition) {
                detectDragGestures(
                    onDragStart = { offset ->
                        if (viewModel.utensil == "path") {
                            viewModel.startPath(offset)
                        }
                    },
                    onDrag = { change, _ ->
                        if (viewModel.utensil == "path") {
                            viewModel.addPathPoint(change.position)
                        }
                        //viewModel.lastDragPosition =  change.position

                    },
                    onDragEnd = {
                        if (viewModel.utensil == "path") {
                            viewModel.endPath()
                        }
//                        else if(viewModel.lastDragPosition != null) {
//                             viewModel.addLabeledPoint(
//                                 viewModel.lastDragPosition!!,
//                                viewModel.utensil
//                            )
//                       }
                    }
                )
            }
    ) {

        // --- Draw committed strokes ---
        paths.forEach { stroke ->
            when (stroke) {

                is Stroke.Path -> {
                    val pts = stroke.points
                    for (i in 1 until pts.size) {
                        drawLine(
                            color = Color.Black,
                            start = pts[i - 1],
                            end = pts[i],
                            strokeWidth = 6f
                        )
                    }
                }

                is Stroke.Labeled -> {
                    val (offset, label) = stroke.points
                    when(label){
                        "shoot"->{
                            drawCircle(
                                color = Color.Yellow,
                                radius = 10f,
                                center = offset
                            )
                        }
                        "intake" -> {
                            val trianglePath = Path().apply {
                                moveTo(offset.x, offset.y - 20f)          // top
                                lineTo(offset.x - 20f, offset.y + 20f)    // bottom-left
                                lineTo(offset.x + 20f, offset.y + 20f)    // bottom-right
                                close()
                            }

                            drawPath(
                                path = trianglePath,
                                color = Color.Blue
                            )
                        }
                        "broke"->{
                            drawRect(
                                color = Color.Red,
                                topLeft = Offset(50f, 50f),
                                size = androidx.compose.ui.geometry.Size(200f, 100f)
                            )
                        }
                    }
                }
            }
        }
        // --- Draw the in-progress stroke (only Path can be in-progress) ---
        if (current is Stroke.Path) {
            val pts = current.points
            for (i in 1 until pts.size) {
                drawLine(
                    color = Color.Black,
                    start = pts[i - 1],
                    end = pts[i],
                    strokeWidth = 6f
                )
            }
        }
    }
}
