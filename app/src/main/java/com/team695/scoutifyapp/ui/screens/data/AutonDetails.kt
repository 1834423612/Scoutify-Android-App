package com.team695.scoutifyapp.ui.screens.data

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Path

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.graphics.ImageBitmap
//import androidx.compose.ui.graphics.drawscope.drawImage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.team695.scoutifyapp.R

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.team695.scoutifyapp.data.types.GameFormState
import com.team695.scoutifyapp.ui.theme.AccentGreen
import com.team695.scoutifyapp.ui.viewModels.DataViewModel
import com.team695.scoutifyapp.ui.viewModels.PenViewModel
import com.team695.scoutifyapp.ui.viewModels.Stroke





import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize


// ─── Root Composable ────────────────────────────────────────────────────────

@Composable
fun AutonDetails(
    dataViewModel: DataViewModel,
    formState: GameFormState,
    viewModel: PenViewModel
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

            TopbarNoButton(title = "Auton and Auton Path")

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
                            text = "Drawing Utensils",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        RobotActionPanel(
                            { viewModel.undo() },
                            {viewModel.redo()},
                            { viewModel.utensil = "path" },
                            { viewModel.utensil = "shoot" },
                            { viewModel.utensil = "intake" },
                            { viewModel.utensil = "broke" },
                            {viewModel.reset()},
                        )
                    }
                }

                ScoutingBubble(
                    modifier = Modifier
                        .weight(2f) // Takes up 2/3 of the screen width
                        .fillMaxHeight()
                ) {
                    Text(
                        text = "Auton Path Map",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val image: ImageBitmap = ImageBitmap.imageResource(id = R.drawable.autonpath)
                    DrawCanvas(viewModel, image)
                }
            }
        }
    }
}


@Composable
fun DrawCanvas(
    viewModel: PenViewModel,
    image: ImageBitmap
) {
    val paths = viewModel.paths
    val current = viewModel.currentStroke
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            //.aspectRatio(2f)
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
        val centerOffset = Offset(
            (size.width - image.width) / 2f,
            (size.height - image.height) / 2f
        )

        drawImage(
            image = image,
            dstSize = IntSize(size.width.toInt(), size.height.toInt())
        )

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
                                topLeft = offset,
                                size = androidx.compose.ui.geometry.Size(50f, 50f)
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

// ── Colors ──────────────────────────────────────────────────────────────────
private val BgPanel   = Color(0xFF1E1E2E)
private val BgButton  = Color(0xFF2A2A3D)
private val BgHover   = Color(0xFF323248)
private val TextColor = Color(0xFFE0E0F0)
private val AccentOrange = Color(0xFFFF6B35)
private val AccentBlue   = Color(0xFF4A9EFF)
private val AccentYellow = Color(0xFFFFD166)
private val BorderActive = Color(0xFFFF8C42)

// ── Shared button shape ──────────────────────────────────────────────────────
private val ButtonShape = RoundedCornerShape(12.dp)

// ── Main composable ──────────────────────────────────────────────────────────
@Composable
fun RobotActionPanel(
    onUndo: () -> Unit = {},
    onRedo: () -> Unit = {},
    onPath: () -> Unit = {},
    onBall: () -> Unit = {},
    onIntake: () -> Unit = {},
    onBroke: () -> Unit = {},
    onReset: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf("Intake") }

    Column(
        modifier = modifier
            .width(180.dp)
            .background(BgPanel, RoundedCornerShape(16.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Undo / Redo row ─────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgButton, ButtonShape)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = onUndo, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Undo, contentDescription = "Undo", tint = TextColor)
            }
            IconButton(onClick = onRedo, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Redo, contentDescription = "Redo", tint = TextColor)
            }
        }

        // ── Path button ─────────────────────────────────────────────────────
        ActionButton(
            label = "Path",
            isSelected = selected == "Path",
            onClick = { selected = "Path"; onPath() }
        ) {
            // Bullet/projectile pointing right (gray body, pointed tip)
            BulletIcon(bodyColor = Color(0xFF888899), tipColor = Color(0xFF555566))
        }

        // ── Ball button ───────────────────────────────────────────
        ActionButton(
            label = "Shoot",
            isSelected = selected == "Ball",
            onClick = { selected = "Ball"; onBall() }
        ) {
            ShootIcon()
        }

        // ── Intake button ────────────────────────────────────────────────────
        ActionButton(
            label = "Intake",
            isSelected = selected == "Intake",
            onClick = { selected = "Intake"; onIntake() },
            borderColor = Color.Transparent
        ) {
            IntakeIcon()
        }

        // ── Broke button ─────────────────────────────────────────────────────
        ActionButton(
            label = "Broke",
            isSelected = selected == "Broke",
            onClick = { selected = "Broke"; onBroke() }
        ) {
            BrokeIcon()
        }

        // ── Reset button ─────────────────────────────────────────────────────
        ActionButton(
            label = "Reset",
            isSelected = selected == "Reset",
            onClick = { selected = "Reset"; onReset() }
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Reset",
                tint = TextColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// ── Generic action button ────────────────────────────────────────────────────
@Composable
private fun ActionButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    borderColor: Color = Color.Transparent,
    content: @Composable BoxScope.() -> Unit
) {
    val border = if (borderColor != Color.Transparent)
        Modifier.border(2.dp, borderColor, ButtonShape)
    else Modifier

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(ButtonShape)
            .background(if (isSelected) BgHover else BgButton)
            .then(border)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center,
            content = content
        )
        if (label.isNotEmpty()) {
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                color = TextColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ── Icon: Bullet / Projectile ────────────────────────────────────────────────
@Composable
private fun BulletIcon(bodyColor: Color, tipColor: Color) {
    Canvas(modifier = Modifier.size(36.dp)) {
        val w = size.width
        val h = size.height
        val cy = h / 2f

        // Body (rectangle)
        val bodyPath = Path().apply {
            moveTo(w * 0.15f, cy - h * 0.18f)
            lineTo(w * 0.70f, cy - h * 0.18f)
            lineTo(w * 0.70f, cy + h * 0.18f)
            lineTo(w * 0.15f, cy + h * 0.18f)
            close()
        }
        drawPath(bodyPath, bodyColor)

        // Pointed tip (right side)
        val tipPath = Path().apply {
            moveTo(w * 0.70f, cy - h * 0.18f)
            lineTo(w * 0.92f, cy)
            lineTo(w * 0.70f, cy + h * 0.18f)
            close()
        }
        drawPath(tipPath, tipColor)
    }
}


@Composable
private fun ShootIcon() {
    Canvas(modifier = Modifier.size(36.dp)) {
        val w = size.width
        val h = size.height

        val barTop = h * 0.2f
        val barBottom = h * 0.8f
        val barHeight = barBottom - barTop

        val rectRight = w * 0.70f
        val radius = barHeight / 2f

        val yellow = Color(0xFFE6D36F)

        // Main black rectangle
        drawRect(
            color = Color.Black,
            topLeft = Offset(0f, barTop),
            size = Size(rectRight, barHeight)
        )

        // White borders (top & bottom)
        drawLine(
            color = Color.White,
            start = Offset(0f, barTop),
            end = Offset(rectRight, barTop),
            strokeWidth = 3f
        )

        drawLine(
            color = Color.White,
            start = Offset(0f, barBottom),
            end = Offset(rectRight, barBottom),
            strokeWidth = 3f
        )

        // Black rounded end (half circle)
        drawArc(
            color = Color.Black,
            startAngle = -90f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(rectRight - radius, barTop),
            size = Size(barHeight, barHeight)
        )

        // Yellow outer ring
        drawArc(
            color = yellow,
            startAngle = -90f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(rectRight - radius - 6f, barTop - 6f),
            size = Size(barHeight + 12f, barHeight + 12f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f)
        )

        // Yellow circular cap at front
        drawCircle(
            color = yellow,
            radius = radius * 0.6f,
            center = Offset(rectRight + radius * 0.6f, h / 2f)
        )
    }
}


@Composable
private fun IntakeIcon() {
    Canvas(modifier = Modifier.size(36.dp)) {
        val w = size.width
        val h = size.height

        // Main black rectangle
        drawRect(
            color = Color.Black,
            size = Size(w * 0.85f, h * 0.7f),
            topLeft = Offset(0f, h * 0.15f)
        )

        // White border (top & bottom)
        drawLine(
            color = Color.White,
            start = Offset(0f, h * 0.15f),
            end = Offset(w * 0.85f, h * 0.15f),
            strokeWidth = 4f
        )

        drawLine(
            color = Color.White,
            start = Offset(0f, h * 0.85f),
            end = Offset(w * 0.85f, h * 0.85f),
            strokeWidth = 4f
        )

        // Light blue angled shape (middle slanted part)
        val bluePath = Path().apply {
            moveTo(w * 0.78f, h * 0.15f)
            lineTo(w * 0.82f, h * 0.15f)
            lineTo(w * 0.79f, h * 0.85f)
            lineTo(w * 0.75f, h * 0.85f)
            close()
        }
        drawPath(bluePath, color = Color(0xFF87B6E6))

        // Right triangle end
        val trianglePath = Path().apply {
            moveTo(w * 0.85f, h * 0.15f)
            lineTo(w * 0.95f, h * 0.5f)
            lineTo(w * 0.85f, h * 0.85f)
            close()
        }
        drawPath(trianglePath, color = Color(0xFF87B6E6))
    }
}
// ── Icon: Broke ───────────────────────────────────────────────────────────────
@Composable
private fun BrokeIcon() {
    Canvas(modifier = Modifier.size(36.dp)) {
//        val w = size.width
//        val h = size.height
//        val cy = h / 2f
//
//        // Red/orange body rectangle
//        drawRect(
//            color = AccentOrange,
//            topLeft = Offset(w * 0.10f, cy - h * 0.20f),
//            size = Size(w * 0.55f, h * 0.40f)
//        )
//        // Small dark stripe (break indicator)
//        drawRect(
//            color = Color(0xFF1E1E2E),
//            topLeft = Offset(w * 0.30f, cy - h * 0.20f),
//            size = Size(w * 0.06f, h * 0.40f)
//        )
        val w = size.width
        val h = size.height

        val barTop = h * 0.2f
        val barBottom = h * 0.8f
        val barRight = w * 0.85f

        // Main black bar
        drawRect(
            color = Color.Black,
            topLeft = Offset(0f, barTop),
            size = Size(barRight, barBottom - barTop)
        )

        // White borders (top & bottom)
        drawLine(
            color = Color.White,
            start = Offset(0f, barTop),
            end = Offset(barRight, barTop),
            strokeWidth = 3f
        )

        drawLine(
            color = Color.White,
            start = Offset(0f, barBottom),
            end = Offset(barRight, barBottom),
            strokeWidth = 3f
        )

        val accentColor = Color(0xFFFF6F5A)

        // Coral slanted strip
        val slanted = Path().apply {
            moveTo(w * 0.78f, barTop)
            lineTo(w * 0.82f, barTop)
            lineTo(w * 0.79f, barBottom)
            lineTo(w * 0.75f, barBottom)
            close()
        }
        drawPath(slanted, accentColor)

        // Right coral angled end
        val endShape = Path().apply {
            moveTo(barRight, barTop)
            lineTo(w * 0.97f, h * 0.35f)
            lineTo(w * 0.95f, barBottom)
            lineTo(barRight, barBottom)
            close()
        }
        drawPath(endShape, accentColor)
    }
}