package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team695.scoutifyapp.R
import com.team695.scoutifyapp.ui.theme.*

@Composable
fun MainScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NavRail()
            MainContent()
        }
    }
}

@Composable
fun NavRail() {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(100.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "background",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = 0f
                    translationY = 450f
                }
                .drawWithCache {
                    val gradient = Brush.radialGradient(
                        colors = listOf(Color.Black, Color.Transparent),
                        radius = size.width * 1.5f
                    )
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush = gradient,
                            blendMode = BlendMode.DstIn
                        )
                    }
                },
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundNav)
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.account), // placeholder
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Text("Clarence", color = TextPrimary, fontSize = 12.sp)
            }

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(BackgroundTertiary),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                NavItem(icon = R.drawable.home, label = "Home", selected = true)
                NavItem(icon = R.drawable.pits, label = "Pit Scout")
                NavItem(icon = R.drawable.upload, label = "Upload")
                NavItem(icon = R.drawable.settings, label = "Settings")
            }

            Spacer(modifier = Modifier.height(1.dp)) // for arrangement
        }
    }
}

@Composable
fun NavItem(icon: Int, label: String, selected: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) Accent.copy(alpha = 0.2f) else Color.Transparent)
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = label,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            color = if (selected) Accent else TextPrimary,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun MainContent() {
    Row(
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.weight(0.4f)) {
            TasksCard()
        }
        Box(modifier = Modifier.weight(0.6f)) {
            MatchSchedule()
        }
    }
}

@Composable
fun TasksCard() {
    var selectedTab by remember { mutableStateOf(0) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "background",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = 0f
                    translationY = 450f
                }
                .drawWithCache {
                    val gradient = Brush.radialGradient(
                        colors = listOf(Color.Black, Color.Transparent),
                        radius = size.width * 0.8f
                    )
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush = gradient,
                            blendMode = BlendMode.DstIn
                        )
                    }
                },
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundCard)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Tasks", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BackgroundTertiary,
                indicator = {},
                divider = {},
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, BorderSecondary, RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = if (selectedTab == 0) Modifier.background(SelectedItem) else Modifier.background(UnSelectedItem),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                        Text("Incomplete", color = TextPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Badge(containerColor = BadgeBackground) { Text("9", color = BadgeContent) }
                    }
                }
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = if (selectedTab == 1) Modifier.background(SelectedItem) else Modifier.background(UnSelectedItem),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                        Text("Done", color = TextSecondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Badge(containerColor = BadgeBackgroundSecondary) { Text("0") }
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

@Composable
fun TaskItem(matchNum: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AccentSecondary, shape = RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = R.drawable.edit), contentDescription = "Edit", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Q$matchNum", color = TextPrimary)
        }
        Text("695", color = TextPrimary)
        Box(modifier = Modifier
            .width(50.dp)
            .height(20.dp)
            .border(1.dp, TextSecondary, RoundedCornerShape(4.dp)))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = R.drawable.clock), contentDescription = "Time", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (matchNum == 5) "40m" else "02m", color = TextPrimary, fontSize = 12.sp)
        }
        Image(painter = painterResource(id = R.drawable.right_arrow), contentDescription = "Go", modifier = Modifier.size(16.dp))
    }
}

@Composable
fun MatchSchedule() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "background",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = 0f
                    translationY = 450f
                }
                .drawWithCache {
                    val gradient = Brush.radialGradient(
                        colors = listOf(Color.Black, Color.Transparent),
                        radius = size.width * 1.5f
                    )
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush = gradient,
                            blendMode = BlendMode.DstIn
                        )
                    }
                },
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundCard)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bluffcountry),
                    contentDescription = "Regional background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            "Bluff Country Regional",
                            color = TextPrimary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Image(
                            painter = painterResource(id = R.drawable.info),
                            contentDescription = "Info",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    TextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Search Team", color = TextSecondary) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = TextFieldBackground,
                            unfocusedContainerColor = TextFieldBackground,
                            disabledContainerColor = TextFieldBackground,
                            cursorColor = TextPrimary,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(0.5f)
                    )
                }
            }
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { MatchItem("Q1", "9:00AM", "9999", "9999", "9999", "9999", "9999", "9999", false) }
                item { MatchItem("Q3", "9:14AM", "9999", "695", "9999", "9999", "9999", "9999", true, R.drawable.bison) }
                item { MatchItem("Q3", "9:14AM", "9999", "9999", "9999", "9999", "9999", "9999", false) }
                item { MatchItem("Q4", "9:21AM", "9999", "4211", "9999", "9999", "9999", "9999", false) }
                item { MatchItem("Q5", "9:28AM", "9999", "9999", "9999", "1787", "9999", "9999", true) }
                item { MatchItem("Q6", "9:35AM", "9999", "9999", "9999", "9999", "9999", "9999", false) }
                item { MatchItem("Q7", "9:42AM", "9999", "9999", "9999", "9999", "254", "9999", true) }
                item { MatchItem("Q7", "9:42AM", "9999", "9999", "9999", "9999", "254", "9999", true) }
                item { MatchItem("Q6", "9:35AM", "9999", "9999", "9999", "9999", "9999", "9999", false) }
            }
        }
    }
}

@Composable
fun TeamNumber(number: String, textColor: Color) {
    val highlightedTeams = setOf("695", "4211", "1787", "254")
    val isHighlighted = number in highlightedTeams
    Text(
        text = number,
        color = if (isHighlighted) BadgeContent else textColor,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(
                if (isHighlighted) Accent else Color.Transparent,
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

@Composable
fun MatchItem(
    matchNum: String,
    time: String,
    red1: String,
    red2: String,
    red3: String,
    blue1: String,
    blue2: String,
    blue3: String,
    isSpecial: Boolean,
    specialIcon: Int? = null
) {
    val borderColor = if (isSpecial) Accent else Border
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (specialIcon != null) {
            Image(
                painterResource(id = specialIcon),
                contentDescription = "Special Icon",
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))

        Row(
            modifier = Modifier
                .border(1.dp, AccentGreen, RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(matchNum, color = TextPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Image(painter = painterResource(id = R.drawable.clock), contentDescription = "time", modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(time, color = TextSecondary, fontSize = 12.sp)
        }


        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamNumber(red1, RedAlliance)
            TeamNumber(red2, RedAlliance)
            TeamNumber(red3, RedAlliance)
        }

        Text(" vs ", color = TextSecondary, modifier = Modifier.padding(horizontal = 4.dp))

        Row(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamNumber(blue1, BlueAlliance)
            TeamNumber(blue2, BlueAlliance)
            TeamNumber(blue3, BlueAlliance)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.buttonColors(containerColor = CommentButtonBackground),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text("Comment", color = TextPrimary, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Image(painterResource(id = R.drawable.comment), contentDescription = "Comment", modifier = Modifier.size(16.dp))
        }
    }
}


@Preview(showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
fun MainScreenPreview() {
    ScoutifyTheme {
        MainScreen()
    }
}
