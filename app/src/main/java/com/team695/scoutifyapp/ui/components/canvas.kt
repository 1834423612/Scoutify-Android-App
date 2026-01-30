import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable

fun Render() {
    var currentPath by remember { mutableStateOf(listOf<Offset>()) }
    var paths by remember { mutableStateOf(listOf<List<Offset>>()) }

    Box(
        modifier = Modifier
            .width(300.dp)
            .height(150.dp)
            .border(2.dp, Color.Gray)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        currentPath = listOf(offset)
                    },
                    onDrag = { change, _ ->
                        currentPath = currentPath + change.position
                    },
                    onDragEnd = {
                        paths = paths + listOf(currentPath)
                        currentPath = emptyList()
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize().clip(RectangleShape)) {
            // Draw completed paths
            paths.forEach { path ->
                drawPathFromOffsets(path)
            }
            // Draw the path currently being drawn
            drawPathFromOffsets(currentPath)
        }
    }
}

fun DrawScope.drawPathFromOffsets(points: List<Offset>) {
    if (points.size < 2) return

    for (i in 0 until points.lastIndex) {
        drawLine(
            color = Color.Black,
            start = points[i],
            end = points[i + 1],
            strokeWidth = 4f
        )
    }
}

