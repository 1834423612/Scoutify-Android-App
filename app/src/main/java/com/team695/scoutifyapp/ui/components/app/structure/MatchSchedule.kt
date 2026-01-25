package com.team695.scoutifyapp.ui.components.app.structure

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team695.scoutifyapp.R
import com.team695.scoutifyapp.ui.modifier.buttonHighlight
import com.team695.scoutifyapp.ui.theme.Accent
import com.team695.scoutifyapp.ui.theme.BadgeBackground
import com.team695.scoutifyapp.ui.theme.BadgeContent
import com.team695.scoutifyapp.ui.theme.BlueAlliance
import com.team695.scoutifyapp.ui.theme.Border
import com.team695.scoutifyapp.ui.theme.DarkGunmetal
import com.team695.scoutifyapp.ui.theme.DarkishGunmetal
import com.team695.scoutifyapp.ui.theme.Deselected
import com.team695.scoutifyapp.ui.theme.LightGunmetal
import com.team695.scoutifyapp.ui.theme.RedAlliance
import com.team695.scoutifyapp.ui.theme.TextFieldBackground
import com.team695.scoutifyapp.ui.theme.TextPrimary
import com.team695.scoutifyapp.ui.theme.TextSecondary

@Composable
fun MatchSchedule() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF171920))
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(8.dp))

    ) {
        ImageBackground(x = -1950f, y = 335f)
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
            .buttonHighlight(
                corner = 4.dp
            )

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
            .background(DarkGunmetal, shape = RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, shape = RoundedCornerShape(8.dp))
            .buttonHighlight(
                corner = 8.dp
            )
            .padding(horizontal = 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(DarkishGunmetal)
                .buttonHighlight(
                    corner = 8.dp
                )
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
                .size(6.dp)
                .graphicsLayer {
                    translationY = 3f
                }
                .background(RedAlliance, shape = CircleShape)
        )

        Text(" vs ", color = TextSecondary, modifier = Modifier.padding(horizontal = 2.dp))
        Box(
            modifier = Modifier
                .size(6.dp)
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
                .width(135.dp)


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
