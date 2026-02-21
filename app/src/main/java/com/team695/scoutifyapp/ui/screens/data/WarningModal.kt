package com.team695.scoutifyapp.ui.screens.data

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.team695.scoutifyapp.data.types.GameFormState
import com.team695.scoutifyapp.ui.theme.RedAlliance

// ─────────────────────────────────────────────
//  Color tokens (override with your theme)
// ─────────────────────────────────────────────
private val BackgroundDim    = Color(0x99000000)   // scrim
private val SurfaceDark      = Color(0xFF1C1C1E)   // modal card
private val SurfaceVariant   = Color(0xFF2C2C2E)   // button well
private val AccentRed        = Color(0xFFFF453A)   // warning / continue
private val AccentRedDim     = Color(0x33FF453A)   // tint on icon bg
private val TextPrimary      = Color(0xFFFFFFFF)
private val TextSecondary    = Color(0xFFAEAEB2)
private val DividerColor     = Color(0xFF3A3A3C)

// ─────────────────────────────────────────────
//  Public composable — drop this anywhere
// ─────────────────────────────────────────────

/**
 * A dark-themed warning modal that alerts the user before starting teleop.
 *
 * Usage:
 *
 *   var showModal by remember { mutableStateOf(false) }
 *
 *   TeleopWarningModal(
 *       visible   = showModal,
 *       onContinue = { showModal = false; startTeleop() },
 *       onCancel   = { showModal = false }
 *   )
 *
 *   Button(onClick = { showModal = true }) { Text("Start Teleop") }
 */
@Composable
fun WarningModal(
    visible: Boolean,
    onContinue: () -> Unit,
    onCancel: () -> Unit,
    formState: GameFormState
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(200)),
        exit  = fadeOut(animationSpec = tween(150)),
    ) {
        Dialog(
            onDismissRequest = onCancel,
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            // Animated card entry
            AnimatedVisibility(
                visible = visible,
                enter = scaleIn(initialScale = 0.92f, animationSpec = tween(220)) +
                        fadeIn(animationSpec = tween(220)),
                exit  = scaleOut(targetScale = 0.92f, animationSpec = tween(150)) +
                        fadeOut(animationSpec = tween(150)),
            ) {
                ModalCard(onContinue = onContinue, onCancel = onCancel, formState = formState)
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Internal card layout
// ─────────────────────────────────────────────

@Composable
private fun ModalCard(
    onContinue: () -> Unit,
    onCancel: () -> Unit,
    formState: GameFormState,
    ) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .width(400.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceDark),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // ── Header ──────────────────────────────────
            Spacer(Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(RedAlliance.copy(0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = "Warning",
                    tint = RedAlliance,
                    modifier = Modifier.size(34.dp),
                )
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text = formState.warningModalTitle,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.3.sp,
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = formState.warningModalText,
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.1.sp,
                modifier = Modifier.padding(horizontal = 24.dp),
            )

            Spacer(Modifier.height(28.dp))

            // ── Divider ──────────────────────────────────
            Divider(color = DividerColor, thickness = 0.5.dp)

            // ── Buttons ──────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth()) {

                // Cancel
                TextButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(
                        topStart = 0.dp, topEnd = 0.dp,
                        bottomStart = 18.dp, bottomEnd = 0.dp,
                    ),
                ) {
                    Text(
                        text = "Cancel",
                        color = TextSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }

                // Vertical divider between buttons
                Box(
                    modifier = Modifier
                        .width(0.5.dp)
                        .height(52.dp)
                        .background(DividerColor),
                )

                // Continue
                TextButton(
                    onClick = onContinue,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(
                        topStart = 0.dp, topEnd = 0.dp,
                        bottomStart = 0.dp, bottomEnd = 18.dp,
                    ),
                ) {
                    Text(
                        text = "Continue",
                        color = RedAlliance,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}