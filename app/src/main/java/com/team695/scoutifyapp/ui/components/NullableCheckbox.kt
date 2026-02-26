package com.team695.scoutifyapp.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.team695.scoutifyapp.ui.theme.Deselected
import com.team695.scoutifyapp.ui.theme.ProgressGreen
import com.team695.scoutifyapp.ui.theme.RedAlliance
import com.team695.scoutifyapp.ui.theme.TextPrimary

@Composable
fun NullableCheckbox(
    state: Boolean?,
    modifier: Modifier = Modifier,
    checkedColor: Color = ProgressGreen,
    uncheckedColor: Color = Deselected,
    unfilledColor: Color = RedAlliance,
    checkmarkColor: Color = TextPrimary
) {
    // Handles the transition from null -> true -> false -> true
    val interactionSource = remember { MutableInteractionSource() }

    // Set up our animation transition
    val transition = updateTransition(targetState = state, label = "checkbox_transition")

    // Animate the background filling in
    val boxColor by transition.animateColor(label = "box_color") { target ->
        when (target) {
            null -> unfilledColor
            true -> checkedColor
            false -> Color.Transparent
        }
    }

    // Animate the border color
    val borderColor by transition.animateColor(label = "border_color") { target ->
        when (target) {
            null -> unfilledColor
            true -> checkedColor
            false -> uncheckedColor
        }
    }

    // Animate the drawing progress of the checkmark (0f to 1f)
    val checkmarkFraction by transition.animateFloat(label = "checkmark_fraction") { target ->
        if (target == true) 1f else 0f
    }

    // Animate the drawing progress of the question mark (0f to 1f)
    val questionMarkFraction by transition.animateFloat(label = "question_fraction") { target ->
        if (target == null) 1f else 0f
    }

    val pathMeasure = remember { PathMeasure() }
    val pathToDraw = remember { Path() }

    Canvas(
        modifier = modifier
            .padding(2.dp) // Standard inner padding
            .size(18.dp)   // Standard inner checkbox size
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

        // 3. Draw the Checkmark (if animating or visible)
        if (checkmarkFraction > 0f) {
            val checkPath = Path().apply {
                moveTo(size.width * 0.25f, size.height * 0.5f)
                lineTo(size.width * 0.45f, size.height * 0.7f)
                lineTo(size.width * 0.75f, size.height * 0.3f)
            }

            pathMeasure.setPath(checkPath, false)
            pathToDraw.reset()
            // Pull only the segment of the path dictated by the animation fraction
            pathMeasure.getSegment(0f, pathMeasure.length * checkmarkFraction, pathToDraw, true)

            drawPath(
                path = pathToDraw,
                color = checkmarkColor,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        // 4. Draw the Question Mark (if animating or visible)
        if (questionMarkFraction > 0f) {
            val questionPath = Path().apply {
                // Top curve of the '?'
                moveTo(size.width * 0.35f, size.height * 0.35f)
                cubicTo(
                    size.width * 0.35f, size.height * 0.15f,
                    size.width * 0.65f, size.height * 0.15f,
                    size.width * 0.65f, size.height * 0.35f
                )
                // Downward stroke to the middle
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
                path = pathToDraw,
                color = checkmarkColor,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Draw the dot of the '?' scaling in with the animation
            if (questionMarkFraction > 0.8f) {
                // Map the last 20% of the animation to a 0f -> 1f scale for the dot
                val dotScale = (questionMarkFraction - 0.8f) * 5f
                drawCircle(
                    color = checkmarkColor,
                    radius = (strokeWidth / 1.5f) * dotScale,
                    center = Offset(size.width * 0.5f, size.height * 0.75f)
                )
            }
        }
    }
}