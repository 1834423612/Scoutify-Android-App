package com.team695.scoutifyapp.ui.screens.data

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team695.scoutifyapp.data.types.ENDGAME_END_TIME
import com.team695.scoutifyapp.data.types.GameFormState
import com.team695.scoutifyapp.ui.theme.AccentGreen
import com.team695.scoutifyapp.ui.theme.BlueAlliance
import com.team695.scoutifyapp.ui.theme.RedAlliance
import com.team695.scoutifyapp.ui.theme.TextPrimary
import com.team695.scoutifyapp.ui.viewModels.DataViewModel
import kotlinx.coroutines.delay

// ─── Color Palette ──────────────────────────────────────────────────────────

private val Background    = Color(0xFF0D0D0F)
private val SurfaceDark   = Color(0xFF161618)
private val SurfaceMid    = Color(0xFF1E1E22)
private val SurfaceLight  = Color(0xFF2A2A30)
private val AccentOrange  = Color(0xFFFF6B35)
private val AccentBlue    = Color(0xFF4DAFFF)
private val AccentGreen   = Color(0xFF3DDC84)
private val TextPrimary   = Color(0xFFEEEEF0)
private val TextSecondary = Color(0xFF888899)
private val BadgeRed      = Color(0xFFE53935)
private val BorderColor   = Color(0xFF2E2E36)

// ─── Root Composable ────────────────────────────────────────────────────────

@Composable
fun EndgameDetails(
    dataViewModel: DataViewModel,
    formState: GameFormState
) {
    var timerRunning by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableStateOf(0) }
    var attemptedClimb by remember { mutableStateOf(true) }
    var succeededClimb by remember { mutableStateOf(true) }


    val timers = listOf(
        Timer(
            label = "Cached Time",
            milliseconds = formState.teleopCachedMilliseconds,
            onClick = {},
            color = RedAlliance
        ),
        Timer(
            label = "Total Time",
            milliseconds = formState.teleopTotalMilliseconds,
            onClick = {},
            color = BlueAlliance
        ),
        Timer(
            label = "Cycling Time",
            milliseconds = formState.gameDetails.endgameCyclingTime ?: 0,
            onClick = {
                dataViewModel.formEvent(
                    gameDetails = formState.gameDetails.copy(
                        endgameCyclingTime = (formState.gameDetails.endgameCyclingTime ?: 0) + formState.teleopCachedMilliseconds
                    )
                )
                dataViewModel.resetCacheTime()
            },
        ),
        Timer(
            label = "Stockpiling Time",
            milliseconds = formState.gameDetails.endgameStockpilingTime ?: 0,
            onClick = {
                dataViewModel.formEvent(
                    gameDetails = formState.gameDetails.copy(
                        endgameStockpilingTime = (formState.gameDetails.endgameStockpilingTime ?: 0) + formState.teleopCachedMilliseconds
                    )
                )
                dataViewModel.resetCacheTime()
            },
        ),
        Timer(
            label = "Defending Time",
            milliseconds = formState.gameDetails.endgameDefendingTime ?: 0,
            onClick = {
                dataViewModel.formEvent(
                    gameDetails = formState.gameDetails.copy(
                        endgameDefendingTime = (formState.gameDetails.endgameDefendingTime ?: 0) + formState.teleopCachedMilliseconds
                    )
                )
                dataViewModel.resetCacheTime()
            },
        ),
        Timer(
            label = "Broken Time",
            milliseconds = formState.gameDetails.endgameBrokenTime ?: 0,
            onClick = {
                dataViewModel.formEvent(
                    gameDetails = formState.gameDetails.copy(
                        endgameBrokenTime = (formState.gameDetails.endgameBrokenTime ?: 0) + formState.teleopCachedMilliseconds
                    )
                )
                dataViewModel.resetCacheTime()
            },
        ),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {

            TeleopTopBar(
                title = "Teleop (Endgame)",
                buttonLabel = "Restart Teleop",
                buttonColor = lerp(
                    start = RedAlliance,
                    stop = AccentGreen,
                    fraction = formState.teleopCachedMilliseconds.toFloat() / ENDGAME_END_TIME
                ),
                onButtonPressed = {
                    //warn user if starting teleop will reset their progress
                    if(formState.gameDetails.teleopProgress > 0) {
                        dataViewModel.toggleWarningModal(title = "Are you sure?", text = "Restarting teleop will reset your data.")
                    }
                    else {
                        dataViewModel.startTeleop()
                    }
                },
                dataViewModel = dataViewModel,
                formState = formState
            )

            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {


                // ── Center panel ──
                TimerPanel(
                    timers = timers
                )

                // ── Right panel ──
                EndgamePanel(
                    attemptedClimb = attemptedClimb,
                    succeededClimb = succeededClimb,
                    onAttemptedChange = { attemptedClimb = it },
                    onSucceededChange = { succeededClimb = it }
                )
            }
        }
    }
}

// ─── Endgame Panel ───────────────────────────────────────────────────────────

@Composable
private fun EndgamePanel(
    attemptedClimb: Boolean,
    succeededClimb: Boolean,
    onAttemptedChange: (Boolean) -> Unit,
    onSucceededChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .width(220.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        // Endgame header button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Endgame",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        CheckboxRow(
            label = "Did Robot attempt climb?",
            checked = attemptedClimb,
            onCheckedChange = onAttemptedChange
        )

        CheckboxRow(
            label = "Did Robot succeed climb?",
            checked = succeededClimb,
            onCheckedChange = onSucceededChange
        )

        // Repeated label (matches screenshot)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Did Robot succeed climb?",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        // Field map placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceMid)
                .border(1.dp, BorderColor, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            FieldDiagram()
        }
    }
}

@Composable
private fun CheckboxRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )

        val checkBg by animateColorAsState(
            targetValue = if (checked) AccentGreen else SurfaceLight,
            animationSpec = tween(200),
            label = "check_bg"
        )

        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(checkBg),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Text("✓", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─── Field Diagram ───────────────────────────────────────────────────────────

@Composable
private fun FieldDiagram() {
    // Simplified top-down FRC field representation
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A3A4A))
            .padding(8.dp)
    ) {
        // Grid lines
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(4) {
                Divider(color = Color(0xFF2A5A6A), thickness = 1.dp)
            }
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(3) {
                VerticalDivider()
            }
        }

        // Corner position buttons
        val positions = listOf(
            Alignment.TopStart, Alignment.TopEnd,
            Alignment.CenterStart, Alignment.CenterEnd,
            Alignment.BottomStart, Alignment.BottomEnd
        )
        positions.forEach { alignment ->
            Box(
                modifier = Modifier.align(alignment).padding(4.dp)
            ) {
                FieldPositionButton()
            }
        }

        // Center label
        Box(
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "REBUILT",
                color = Color(0xFFFF6B35),
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun FieldPositionButton() {
    var selected by remember { mutableStateOf(false) }
    val bg by animateColorAsState(
        targetValue = if (selected) AccentOrange else SurfaceLight,
        animationSpec = tween(150),
        label = "pos_bg"
    )
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(1.dp, BorderColor, RoundedCornerShape(6.dp))
            .clickable { selected = !selected }
    )
}

@Composable
private fun VerticalDivider() {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
            .background(Color(0xFF2A5A6A))
    )
}

// ─── Theme Wrapper ───────────────────────────────────────────────────────────

@Composable
fun ScoutingAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Background,
            surface = SurfaceDark,
            primary = AccentGreen,
            secondary = AccentBlue,
            onBackground = TextPrimary,
            onSurface = TextPrimary
        ),
        content = content
    )
}
