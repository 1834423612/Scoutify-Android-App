package com.frc.scouting

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

// ─── Data Models ────────────────────────────────────────────────────────────

enum class GameSection(val label: String) {
    PRE_GAME("Pre-game"),
    AUTONOMOUS("Autonomous"),
    TELEOPERATED("Teleoperated"),
    POST_GAME("Post-game")
}

data class TimerEntry(
    val label: String,
    val time: String,
    val color: Color = TextPrimary
)

// ─── Root Composable ────────────────────────────────────────────────────────

@Composable
fun ScoutingAppScreen() {
    var selectedSection by remember { mutableStateOf(GameSection.TELEOPERATED) }
    var timerRunning by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableStateOf(0) }
    var attemptedClimb by remember { mutableStateOf(true) }
    var succeededClimb by remember { mutableStateOf(true) }

    LaunchedEffect(timerRunning) {
        while (timerRunning) {
            delay(1000)
            elapsedSeconds++
        }
    }

    fun formatTime(totalSeconds: Int): String {
        val min = totalSeconds / 60
        val sec = totalSeconds % 60
        return "%d:%02d:%02d".format(0, min, sec)
    }

    val timers = listOf(
        TimerEntry("Cached Time",     "0:05:32", AccentOrange),
        TimerEntry("Total Time",      formatTime(elapsedSeconds), AccentBlue),
        TimerEntry("Cycling Time",    "0:04:22"),
        TimerEntry("Stockpiling Time","0:00:00"),
        TimerEntry("Defending Time",  "0:00:00"),
        TimerEntry("Broken Time",     "0:00:00"),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Left sidebar ──
            SidebarColumn(
                selectedSection = selectedSection,
                onSectionSelected = { selectedSection = it }
            )

            // ── Center panel ──
            CenterPanel(
                timers = timers,
                timerRunning = timerRunning,
                onToggleTimer = {
                    timerRunning = !timerRunning
                    if (!timerRunning) elapsedSeconds = 0
                }
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

// ─── Sidebar ─────────────────────────────────────────────────────────────────

@Composable
private fun SidebarColumn(
    selectedSection: GameSection,
    onSectionSelected: (GameSection) -> Unit
) {
    Column(
        modifier = Modifier
            .width(170.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        Text(
            text = "Game Section",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Divider(color = BorderColor, thickness = 1.dp)

        Spacer(Modifier.height(4.dp))

        GameSection.entries.forEach { section ->
            SidebarItem(
                label = section.label,
                selected = section == selectedSection,
                onClick = { onSectionSelected(section) }
            )
        }
    }
}

@Composable
private fun SidebarItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) SurfaceLight else SurfaceDark,
        animationSpec = tween(200),
        label = "sidebar_bg"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(
                width = if (selected) 1.dp else 0.dp,
                color = if (selected) BorderColor else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = if (selected) TextPrimary else TextSecondary,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )

        // Badge
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(BadgeRed),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "P",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ─── Center Panel ────────────────────────────────────────────────────────────

@Composable
private fun CenterPanel(
    timers: List<TimerEntry>,
    timerRunning: Boolean,
    onToggleTimer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Transition Period:",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            val btnBg by animateColorAsState(
                targetValue = if (timerRunning) Color(0xFFCC3333) else Color(0xFF2A6B3C),
                animationSpec = tween(300),
                label = "btn_bg"
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(btnBg)
                    .clickable(onClick = onToggleTimer)
                    .padding(horizontal = 36.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (timerRunning) "Stop" else "Start",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Divider(color = BorderColor)

        // Timer grid
        val rows = timers.chunked(2)
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { entry ->
                    TimerCard(entry = entry, modifier = Modifier.weight(1f))
                }
                // If only 1 item in last row, fill space
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun TimerCard(entry: TimerEntry, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceMid)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Clock icon placeholder
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .border(2.dp, entry.color.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🕐", fontSize = 16.sp)
            }

            Column {
                Text(
                    text = entry.time,
                    color = entry.color,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = entry.label,
                    color = TextSecondary,
                    fontSize = 12.sp
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

// ─── Preview ─────────────────────────────────────────────────────────────────

@Preview(
    showBackground = true,
    widthDp = 900,
    heightDp = 620,
    backgroundColor = 0xFF0D0D0F
)
@Composable
fun ScoutingAppPreview() {
    ScoutingAppTheme {
        ScoutingAppScreen()
    }
}

// ─── MainActivity usage ──────────────────────────────────────────────────────
/*
  In your MainActivity.kt:

  class MainActivity : ComponentActivity() {
      override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          setContent {
              ScoutingAppTheme {
                  Surface(modifier = Modifier.fillMaxSize()) {
                      ScoutingAppScreen()
                  }
              }
          }
      }
  }

  Required dependencies in build.gradle.kts (app):
  implementation("androidx.compose.material3:material3")
  implementation("androidx.compose.animation:animation")
  implementation("androidx.activity:activity-compose:1.8.2")
*/