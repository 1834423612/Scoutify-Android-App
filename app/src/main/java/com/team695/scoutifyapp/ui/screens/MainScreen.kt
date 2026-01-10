package com.team695.scoutifyapp.ui.screens

import android.hardware.lights.Light
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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

    val roundedShape = RoundedCornerShape(
        topStart = 0.dp,
        bottomStart = 0.dp,
        topEnd = 9999.dp,
        bottomEnd = 9999.dp
    )
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(width=1.dp, color=LightGunmetal, shape = RoundedCornerShape(8.dp))
    ) {
        ImageBackground(x=350f, y=325f)
        BackgroundGradient()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(175.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
                
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(
                            CircleShape
                        )
                        .border(width = 1.dp, color=LightGunmetal, shape=CircleShape)
                        .background(Gunmetal),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.account), // placeholder
                        contentDescription = "User Avatar",
                        colorFilter = ColorFilter.tint(Accent),
                        modifier = Modifier
                            .size(30.dp)
                     )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(19.dp)
                        .clip(shape = RoundedCornerShape(size=4.dp))
                        .border(width = 1.dp, color=LightGunmetal, shape=RoundedCornerShape(size=4.dp))
                        .background(Gunmetal),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,

                ) {
                    Text(
                        modifier = Modifier
                            .graphicsLayer(
                                translationY = -5f,
                            ),
                        text="Clarence",
                        color = TextPrimary,
                        fontSize = 9.sp)
                }
            }

            Box(
                modifier = Modifier.wrapContentWidth(unbounded = true)
            ) {
                Column(
                    modifier = Modifier
                        .width(140.dp) // or any width you want for your sidebar
                        .graphicsLayer {
                            translationX = -110f
                            translationY = 0f
                        }
                        .clip(
                            roundedShape
                        )
                        .background(BackgroundTertiary)
                        .border(width=1.dp, color=LightGunmetal, shape = roundedShape)
                        .padding(vertical = 45.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                translationX = 110f
                                translationY = 0f
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        NavItem(icon = R.drawable.home, label = "Home", selected = true)
                        NavItem(icon = R.drawable.pits, label = "Pit Scout")
                        NavItem(icon = R.drawable.upload, label = "Upload")
                        NavItem(icon = R.drawable.settings, label = "Settings")
                    }
                }
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
            .background( Color.Transparent)
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(if (selected) Accent else Deselected)
        )
        Text(
            text = label,
            color = if (selected) Accent else TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal
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
        Box(modifier = Modifier.weight(0.33f)) {
            TasksCard()
        }
        Box(modifier = Modifier.weight(0.67f)) {
            MatchSchedule()
        }
    }
}

@Composable
fun TasksCard() {
    var selectedTab by remember { mutableIntStateOf(0) }
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(8.dp))

    ) {
        ImageBackground(x=-350f, y=330f)
        BackgroundGradient()
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    .clip(RoundedCornerShape(8.dp))
                    .border(2.dp, LightGunmetal, RoundedCornerShape(8.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = if (selectedTab == 0) Modifier.background(Gunmetal) else Modifier.background(
                        UnSelectedItem
                    ),
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
                    modifier = if (selectedTab == 1) Modifier.background(SelectedItem) else Modifier.background(
                        UnSelectedItem
                    ),
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
            .height(45.dp)
            .border(1.dp, LightGunmetal, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
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
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
                .background(DarkishGunmetal)
                .width(30.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.right_arrow),
                contentDescription = "Go",
                colorFilter = ColorFilter.tint(Deselected),
                modifier = Modifier.size(25.dp)
            )}

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
    ) {

        items(count=4) { index ->
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .padding(vertical=6.dp)
                    .border(1.dp, Deselected.copy(0.5f), RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
fun MatchSchedule() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF171920))
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(8.dp))

    ) {
        ImageBackground(x=-1950f, y=335f)
        BackgroundGradient()
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Badge(
                    containerColor = BadgeBackground.copy(0.5f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) { Text("i", color = BadgeContent) }
                Image(
                    painter = painterResource(id = R.drawable.bluffcountry),
                    contentDescription = "Regional background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.3f)
                        .clip(RoundedCornerShape(8.dp))
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Bluff Country Regional",
                                color = TextPrimary,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Image(
                            painter = painterResource(id = R.drawable.info),
                            contentDescription = "Info",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    BasicTextField(
                        value = "",
                        onValueChange = {},
                        // BasicTextField uses a Brush for the cursor
                        cursorBrush = SolidColor(TextPrimary),
                        textStyle = TextStyle(
                            color = TextPrimary,
                            fontSize = 14.sp // Adjust font size to fit the smaller height
                        ),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                // Leading Icon
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = TextSecondary,
                                    modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                                )

                                // Placeholder and Actual Text
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .padding(start=10.dp)
                                ) {
                                    // Show placeholder only when text is empty
                                    //if (value.isEmpty()) {
                                        Text(
                                            text = "Search Team",
                                            color = TextSecondary,
                                            fontSize = 14.sp
                                        )
                                    //}
                                    // This invokes the actual input field
                                    innerTextField()
                                }
                            }
                        },
                        modifier = Modifier
                            .width(200.dp)
                            .height(35.dp) // BasicTextField respects exact heights better
                            .background(
                                color = TextFieldBackground,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = LightGunmetal,
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                }
            }
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    MatchItem(
                        "Q1",
                        "9:00AM",
                        "9999",
                        "9999",
                        "9999",
                        "9999",
                        "9999",
                        "9999",
                        false
                    )
                }
                item {
                    MatchItem(
                        "Q3",
                        "9:14AM",
                        "9999",
                        "695",
                        "9999",
                        "9999",
                        "9999",
                        "9999",
                        true,
                    )
                }
                item {
                    MatchItem(
                        "Q3",
                        "9:14AM",
                        "9999",
                        "9999",
                        "9999",
                        "9999",
                        "9999",
                        "9999",
                        false
                    )
                }
                item {
                    MatchItem(
                        "Q4",
                        "9:21AM",
                        "9999",
                        "4211",
                        "9999",
                        "9999",
                        "9999",
                        "9999",
                        false
                    )
                }
                item {
                    MatchItem(
                        "Q5",
                        "9:28AM",
                        "9999",
                        "9999",
                        "9999",
                        "1787",
                        "9999",
                        "9999",
                        false
                    )
                }
                item {
                    MatchItem(
                        "Q6",
                        "9:35AM",
                        "9999",
                        "9999",
                        "9999",
                        "9999",
                        "9999",
                        "9999",
                        false
                    )
                }
                item {
                    MatchItem(
                        "Q7",
                        "9:42AM",
                        "9999",
                        "9999",
                        "9999",
                        "9999",
                        "254",
                        "9999",
                        false
                    )
                }
                item {
                    MatchItem(
                        "Q7",
                        "9:42AM",
                        "9999",
                        "9999",
                        "9999",
                        "9999",
                        "254",
                        "9999",
                        false
                    )
                }
                item {
                    MatchItem(
                        "Q6",
                        "9:35AM",
                        "9999",
                        "9999",
                        "9999",
                        "9999",
                        "9999",
                        "9999",
                        false
                    )
                }
            }
        }
    }
}

@Composable
fun TeamNumber(number: String) {
    val highlightedTeams = setOf("695", "4211", "1787", "254")
    val isHighlighted = number in highlightedTeams
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(if (isHighlighted) Accent.copy(0.3f) else DarkishGunmetal,
                RoundedCornerShape(4.dp))
            .fillMaxHeight()
            .width(50.dp)
            .padding(horizontal = 5.dp)

    ) {
        Text(
            text = number,
            color = if (isHighlighted) Accent else Deselected,

        )
    }
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
) {
    val borderColor = if (isSpecial) Accent else Border
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .border(1.dp, borderColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(DarkishGunmetal)
                .width(135.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(matchNum, color = Deselected, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = R.drawable.clock),
                contentDescription = "time",
                colorFilter = ColorFilter.tint(Deselected),
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(time, color = Deselected, fontSize = 16.sp)
        }


        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamNumber(red1)
            TeamNumber(red2)
            TeamNumber(red3)
        }

        Box(
            modifier = Modifier
                .size(8.dp)
                .graphicsLayer {
                    translationY = 3f
                }
                .background(RedAlliance, shape = CircleShape)
        )

        Text(" vs ", color = TextSecondary, modifier = Modifier.padding(horizontal = 2.dp))
        Box(
            modifier = Modifier
                .size(8.dp)
                .graphicsLayer {
                    translationY = 3f
                }
                .background(BlueAlliance, shape = CircleShape)
        )
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamNumber(blue1)
            TeamNumber(blue2)
            TeamNumber(blue3)
        }



        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.buttonColors(containerColor = DarkishGunmetal),
            shape = RoundedCornerShape(4.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .fillMaxHeight()
                .width(135.dp),


        ) {

            Text(
                "Comment",
                color = Deselected,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painterResource(id = R.drawable.comment),
                contentScale = ContentScale.Fit,
                contentDescription = "Comment",
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical=8.dp)


            )
        }
    }
}

@Composable
fun ImageBackground(x: Float, y: Float) {
    val fadeBrush = Brush.horizontalGradient(
        0.0f to Color.Black,   // Start fully visible
        0.6f to Color.Black,
        1.0f to Color.Transparent // Fade to transparent at the 100% mark
    )

    Box(
        modifier = Modifier.wrapContentWidth(unbounded = true)
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "background",
            modifier = Modifier
                .width(1500.dp)
                .graphicsLayer {
                    translationX = x
                    translationY = y
                }
                .drawWithContent {
                    // 3. Draw the actual image first
                    drawContent()

                    // 4. Draw the gradient over it with DstIn blend mode
                    drawRect(
                        brush = fadeBrush,
                        blendMode = BlendMode.DstIn
                    )
                },

            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun BackgroundGradient() {
        Box(
            modifier = Modifier
                .wrapContentWidth(unbounded = true)
                .fillMaxSize()
                .width(1000.dp)
                .background(Brush.verticalGradient(
                    colors = listOf(
                        PaneColor,
                        PaneColor.copy(alpha=0.75f)
                    )
                ))

        ) {
        }
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
fun MainScreenPreview() {
    ScoutifyTheme {
        MainScreen()
    }
}
