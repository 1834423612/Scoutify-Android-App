package com.team695.scoutifyapp.ui.components.app.structure

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team695.scoutifyapp.R
import com.team695.scoutifyapp.ui.components.app.reusables.Pressable
import com.team695.scoutifyapp.ui.modifier.buttonHighlight
import com.team695.scoutifyapp.ui.theme.BadgeBackground
import com.team695.scoutifyapp.ui.theme.BadgeBackgroundSecondary
import com.team695.scoutifyapp.ui.theme.BadgeContent
import com.team695.scoutifyapp.ui.theme.Background
import com.team695.scoutifyapp.ui.theme.DarkGunmetal
import com.team695.scoutifyapp.ui.theme.DarkishGunmetal
import com.team695.scoutifyapp.ui.theme.Deselected
import com.team695.scoutifyapp.ui.theme.Gunmetal
import com.team695.scoutifyapp.ui.theme.LightGunmetal
import com.team695.scoutifyapp.ui.theme.ProgressGreen
import com.team695.scoutifyapp.ui.theme.TextPrimary
import com.team695.scoutifyapp.ui.theme.TextSecondary

@Composable
fun TasksCard() {
    var selectedTab by remember { mutableIntStateOf(0) }
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(8.dp))

    ) {
        ImageBackground(x = -350f, y = 330f)
        BackgroundGradient()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Tasks", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Box (
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, LightGunmetal, RoundedCornerShape(8.dp))
                    .background(Background)
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    indicator = {},
                    divider = {},
                    modifier = Modifier
                        .padding(all = 6.dp)

                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = if (selectedTab == 0) Modifier
                            .background(
                                Gunmetal,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .buttonHighlight(
                                corner = 8.dp
                            ) else Modifier.background(Color.Transparent),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text("Incomplete", color = TextPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Badge(
                                containerColor = BadgeBackground,
                                modifier = Modifier
                                    .graphicsLayer {
                                        translationX = -10f
                                        translationY = -10f
                                    }
                            ) { Text("9", color = BadgeContent) }
                        }
                    }
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = if (selectedTab == 1) Modifier.background(
                            Gunmetal,
                            shape = RoundedCornerShape(8.dp)
                        ) else Modifier.background(Color.Transparent),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text("Done", color = TextSecondary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Badge(
                                containerColor = BadgeBackgroundSecondary,
                                modifier = Modifier
                                    .graphicsLayer {
                                        translationX = -10f
                                        translationY = -10f
                                    }
                            ) { Text("0", color = LightGunmetal) }
                        }
                    }

                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(4) { index ->
                    TaskItem(matchNum = index + 2)
                }
            }
        }
    }
}

enum class BorderStyle(val brush: Brush) {
    INCOMPLETE(
        Brush.linearGradient(
            colorStops = arrayOf(
                0f to DarkishGunmetal,
                0f to DarkishGunmetal,
            )
        ),
    ),
    PARTIAL(
        Brush.linearGradient(
            colorStops = arrayOf(
                0f to ProgressGreen,
                1f to DarkishGunmetal,
            )
        ),
    ),
    COMPLETE(
        Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to ProgressGreen,
                        1f to ProgressGreen,
                        )
                ),
    )
}


@Composable
@Preview
fun TaskItem(matchNum: Int = 3, taskCompPercentString: String = "PARTIAL") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .border(
                2.dp,
                BorderStyle.valueOf(taskCompPercentString).brush,
                shape = RoundedCornerShape(8.dp)
            )
            .background(color = DarkishGunmetal, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .buttonHighlight(
                corner = 4.dp
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
                .background(DarkishGunmetal)
                .width(64.dp)
                .buttonHighlight(
                    corner = 4.dp
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.edit),
                colorFilter = ColorFilter.tint(Deselected),
                contentDescription = "Edit",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Q$matchNum", color = Deselected)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
                .background(DarkishGunmetal)
                .width(45.dp)
                .buttonHighlight(
                    corner = 4.dp
                )
        ) {
            Text("695", color = Deselected)
        }
        ProgressIndicator()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
                .background(DarkishGunmetal)
                .width(85.dp)
                .buttonHighlight(
                    corner = 4.dp
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.clock),
                contentDescription = "Time",
                modifier = Modifier
                    .size(16.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text("02m", color = Deselected)
        }

        Pressable (
            onClick = {},
            corner = 4.dp,
            text = "",
            modifier = Modifier
                .fillMaxHeight()
                .width(30.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.right_arrow),
                contentDescription = "Go",
                colorFilter = ColorFilter.tint(Deselected),
                modifier = Modifier.size(25.dp)
            )
        }
    }
}

@Composable
fun ProgressIndicator() {
    LazyRow(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxHeight()
            .width(55.dp)
            .background(DarkishGunmetal, RoundedCornerShape(4.dp))
            .buttonHighlight(
                corner = 4.dp
            )
    ) {

        items(count=4) { index ->
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .padding(vertical = 6.dp)
                    .border(1.dp, Deselected.copy(0.5f), RoundedCornerShape(4.dp))
            )
        }
    }
}
