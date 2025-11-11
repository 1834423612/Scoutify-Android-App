import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Render() {
    Canvas(
        modifier = Modifier
        .width(300.dp)
        .height(150.dp)
        .border(2.dp, color = Color.Gray))
    {
        //example picture
        drawCircle(
            color = Color.Red,
            radius = 100f,
            center = center
        )
        drawLine(
            color = Color.Blue,
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height),
            strokeWidth = 5f
        )
        drawRect(
            color = Color.Green,
            topLeft = Offset(50f, 50f),
            size = androidx.compose.ui.geometry.Size(200f, 100f)
        )
    }
}
