package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team695.scoutifyapp.ui.PenViewModel
import com.team695.scoutifyapp.ui.components.InfoCard
import com.team695.scoutifyapp.ui.components.Bison


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
                }

                Row {
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
    val current = viewModel.currentPath

    Canvas(
        modifier = Modifier
            .width(300.dp)
            .height(150.dp)
            .border(2.dp, Color.Gray)
            .clip(RectangleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        viewModel.startStroke(offset)
                    },
                    onDrag = { change, _ ->
                        viewModel.addPoint(change.position)
                    },
                    onDragEnd = {
                        viewModel.endStroke()
                    }
                )
            }
    ) {

        // Draw committed strokes
        paths.forEach { path ->
            for (i in 1 until path.size) {
                drawLine(
                    color = Color.Black,
                    start = path[i - 1],
                    end = path[i],
                    strokeWidth = 6f
                )
            }
        }

        // Draw the in-progress stroke
        for (i in 1 until current.size) {
            drawLine(
                color = Color.Black,
                start = current[i - 1],
                end = current[i],
                strokeWidth = 6f
            )
        }
    }
}