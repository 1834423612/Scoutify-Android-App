package com.team695.scoutifyapp.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.team695.scoutifyapp.ui.theme.Deselected
import com.team695.scoutifyapp.ui.theme.ProgressGreen
import com.team695.scoutifyapp.ui.theme.RedAlliance
import com.team695.scoutifyapp.ui.theme.TextPrimary

// Helper data class to manage the combined transition state cleanly
private data class CheckboxState(val checked: Boolean?, val locked: Boolean)

@Composable
fun NullableCheckbox(
    state: Boolean?,
    locked: Boolean = false, // New locked parameter
    modifier: Modifier = Modifier,
    checkedColor: Color = ProgressGreen,
    uncheckedColor: Color = Deselected,
    unfilledColor: Color = RedAlliance,
    checkmarkColor: Color = TextPrimary
) {
    val interactionSource = remember { MutableInteractionSource() }

    // Tie both states into a single transition to coordinate fading/animating
    val transition = updateTransition(
        targetState = CheckboxState(state, locked),
        label = "checkbox_transition"
    )

    // Fade the background to 50% opacity if locked to indicate disabled state
    val boxColor by transition.animateColor(label = "box_color") { target ->
        val baseColor = when (target.checked) {
            null -> unfilledColor
            true -> checkedColor
            false -> Color.Transparent
        }
        if (target.locked) baseColor.copy(alpha = 0.5f) else baseColor
    }

    val borderColor by transition.animateColor(label = "border_color") { target ->
        val baseColor = when (target.checked) {
            null -> unfilledColor
            true -> checkedColor
            false -> uncheckedColor
        }
        if (target.locked) baseColor.copy(alpha = 0.5f) else baseColor
    }

    // Only draw the checkmark if true AND not locked
    val checkmarkFraction by transition.animateFloat(label = "checkmark_fraction") { target ->
        if (target.checked == true && !target.locked) 1f else 0f
    }

    // Only draw the question mark if null AND not locked
    val questionMarkFraction by transition.animateFloat(label = "question_fraction") { target ->
        if (target.checked == null && !target.locked) 1f else 0f
    }

    // Animate the drawing progress of the lock (0f to 1f)
    val lockFraction by transition.animateFloat(label = "lock_fraction") { target ->
        if (target.locked) 1f else 0f
    }

    val pathMeasure = remember { PathMeasure() }
    val pathToDraw = remember { Path() }

    Canvas(
        modifier = modifier
            .padding(2.dp)
            .size(18.dp)
    ) {
        val strokeWidth = 2.dp.toPx()
        val cornerRadius = CornerRadius(2.dp.toPx())

        // 1. Draw the Background (Filled)
        drawRoundRect(
            color = boxColor,
            size = size,
            cornerRadius = cornerRadius,
            style = Fill
        )

        // 2. Draw the Border
        drawRoundRect(
            color = borderColor,
            size = size,
            cornerRadius = cornerRadius,
            style = Stroke(width = strokeWidth)
        )

        // 3. Draw the Checkmark
        if (checkmarkFraction > 0f) {
            val checkPath = Path().apply {
                moveTo(size.width * 0.25f, size.height * 0.5f)
                lineTo(size.width * 0.45f, size.height * 0.7f)
                lineTo(size.width * 0.75f, size.height * 0.3f)
            }

            pathMeasure.setPath(checkPath, false)
            pathToDraw.reset()
            pathMeasure.getSegment(0f, pathMeasure.length * checkmarkFraction, pathToDraw, true)

            drawPath(
                path = pathToDraw, color = checkmarkColor,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        // 4. Draw the Question Mark
        if (questionMarkFraction > 0f) {
            val questionPath = Path().apply {
                moveTo(size.width * 0.35f, size.height * 0.35f)
                cubicTo(
                    size.width * 0.35f, size.height * 0.15f,
                    size.width * 0.65f, size.height * 0.15f,
                    size.width * 0.65f, size.height * 0.35f
                )
                cubicTo(
                    size.width * 0.65f, size.height * 0.45f,
                    size.width * 0.5f, size.height * 0.45f,
                    size.width * 0.5f, size.height * 0.6f
                )
            }

            pathMeasure.setPath(questionPath, false)
            pathToDraw.reset()
            pathMeasure.getSegment(0f, pathMeasure.length * questionMarkFraction, pathToDraw, true)

            drawPath(
                path = pathToDraw, color = checkmarkColor,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            if (questionMarkFraction > 0.8f) {
                val dotScale = (questionMarkFraction - 0.8f) * 5f
                drawCircle(
                    color = checkmarkColor,
                    radius = (strokeWidth / 1.5f) * dotScale,
                    center = Offset(size.width * 0.5f, size.height * 0.75f)
                )
            }
        }

        // 5. Draw the Padlock (if animating or visible)
        if (lockFraction > 0f) {
            val lockPath = Path().apply {
                val bodyLeft = size.width * 0.25f
                val bodyRight = size.width * 0.75f
                val bodyTop = size.height * 0.4f
                val bodyBottom = size.height * 0.8f

                val shackleLeft = size.width * 0.35f
                val shackleRight = size.width * 0.65f
                val shackleStraightTop = size.height * 0.25f

                // Shackle Left Leg
                moveTo(shackleLeft, bodyTop)
                lineTo(shackleLeft, shackleStraightTop)

                // Shackle Arch
                arcTo(
                    rect = Rect(
                        left = shackleLeft,
                        top = size.height * 0.1f,
                        right = shackleRight,
                        bottom = size.height * 0.4f
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = false
                )

                // Shackle Right Leg
                lineTo(shackleRight, bodyTop)

                // Lock Body (Drawn continuously for PathMeasure)
                lineTo(bodyRight, bodyTop)
                lineTo(bodyRight, bodyBottom)
                lineTo(bodyLeft, bodyBottom)
                lineTo(bodyLeft, bodyTop)
                lineTo(shackleLeft, bodyTop)
            }

            pathMeasure.setPath(lockPath, false)
            pathToDraw.reset()
            pathMeasure.getSegment(0f, pathMeasure.length * lockFraction, pathToDraw, true)

            drawPath(
                path = pathToDraw, color = uncheckedColor,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Draw a tiny vertical line as the keyhole
            if (lockFraction > 0.8f) {
                val keyholeScale = (lockFraction - 0.8f) * 5f
                drawLine(
                    color = checkmarkColor,
                    start = Offset(size.width * 0.5f, size.height * 0.55f),
                    end = Offset(size.width * 0.5f, size.height * 0.65f),
                    strokeWidth = strokeWidth * keyholeScale,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}