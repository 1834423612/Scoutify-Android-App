package com.team695.scoutifyapp.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.team695.scoutifyapp.R
import com.team695.scoutifyapp.data.api.model.Match
import com.team695.scoutifyapp.data.extensions.convertIsoToUnix
import com.team695.scoutifyapp.ui.reusables.Pressable
import com.team695.scoutifyapp.ui.components.BackgroundGradient
import com.team695.scoutifyapp.ui.components.ImageBackground
import com.team695.scoutifyapp.ui.components.buttonHighlight
import com.team695.scoutifyapp.ui.extensions.convertUnixToMilitaryTime
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
import com.team695.scoutifyapp.ui.theme.mediumCornerRadius
import com.team695.scoutifyapp.ui.theme.smallCornerRadius
import com.team695.scoutifyapp.ui.viewModels.HomeViewModel
import kotlin.math.abs
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

@Composable
fun MatchSchedule(homeViewModel: HomeViewModel, modifier: Modifier = Modifier, onCommentClicked: () -> Unit) {
    var searchQuery: String by remember { mutableStateOf("") }
    val matchState by homeViewModel.matchState.collectAsStateWithLifecycle()
    val readyState by homeViewModel.isReady.collectAsStateWithLifecycle()
    val teamState by homeViewModel.teamsState.collectAsStateWithLifecycle()

    val filteredMatches = remember(searchQuery, matchState) {
        matchState?.sorted()?.filter { m ->
            searchQuery.isBlank()
                    || m.redAlliance.any{ it.toString().contains(searchQuery) }
                    || m.blueAlliance.any{ it.toString().contains(searchQuery) }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .clip(RoundedCornerShape(smallCornerRadius))
            .border(1.dp, LightGunmetal, RoundedCornerShape(smallCornerRadius))

    ) {
        ImageBackground(x = -1350f, y = 355f)
        BackgroundGradient()
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp) // Changed back to a fixed height
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bluffcountry),
                    contentDescription = "Regional background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize() // Safe to use fillMaxSize again with fixed height
                        .alpha(0.3f)
                        .clip(RoundedCornerShape(smallCornerRadius))
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize() // Fills the 150dp Box height evenly
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // 1. Header Title
                        Text(
                            text = "Bluff Country Regional",
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start,
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Info Icon
                        Badge(
                            containerColor = BadgeBackground.copy(0.5f),
                            modifier = Modifier
                                .padding(1.dp)
                                .weight(1f)
                        ) {
                            Text(
                                text = "i",
                                color = BadgeContent,
                                fontSize = 14.sp,
                            )
                        }

                        Spacer(modifier = Modifier.width(384.dp))

                        // 2. Search Bar
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            cursorBrush = SolidColor(TextPrimary),
                            textStyle = TextStyle(
                                color = TextPrimary,
                                fontSize = 14.sp
                            ),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = TextSecondary,
                                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                                    )

                                    Box(
                                        contentAlignment = Alignment.CenterStart,
                                        modifier = Modifier.padding(start = 2.dp)
                                    ) {
                                        if (searchQuery.isEmpty()) {
                                            Text(
                                                text = "Search Team",
                                                color = TextSecondary,
                                                fontSize = 14.sp
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            },
                            modifier = Modifier
                                .width(160.dp) // Shrunk slightly to fit cleanly next to the title
                                .height(35.dp)
                                .background(
                                    color = TextFieldBackground,
                                    shape = RoundedCornerShape(smallCornerRadius)
                                )
                                .border(
                                    width = 1.dp,
                                    color = LightGunmetal,
                                    shape = RoundedCornerShape(smallCornerRadius)
                                )
                        )
                    }

                    if (!teamState.isNullOrEmpty()) {
                        Text(
                            text = "Currently Scouting:",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(35.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            items(teamState ?: emptyList()) { team ->
                                TeamNumber(number = team.toString())
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            if (matchState.isNullOrEmpty() || !readyState) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(
                            color = TextPrimary
                        )
                        Text(
                            text = "Loading schedule...",
                            color = TextSecondary,
                            fontSize = 16.sp
                        )
                    }
                }
            } else if (filteredMatches?.isEmpty() ?: true) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No matches found for team '$searchQuery'",
                        color = TextSecondary,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(smallCornerRadius)
                ) {

                    val highlightedMatch = calculateHighlight(
                        matches = filteredMatches,
                        time = System.currentTimeMillis()
                    )

                    items(filteredMatches) {
                        MatchItem(
                            matchNum = it.matchNumber,
                            time = it.time.toLong(),
                            redAlliance = it.redAlliance,
                            blueAlliance = it.blueAlliance,
                            showHighlight = highlightedMatch.matchNumber == it.matchNumber,
                            onCommentClicked = onCommentClicked
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TeamNumber(number: String) {
    val highlightedTeams = setOf("695", "1787")
    val isHighlighted = number in highlightedTeams
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(if (isHighlighted) Accent.copy(0.3f) else DarkishGunmetal,
                RoundedCornerShape(smallCornerRadius))
            .fillMaxHeight()
            .width(50.dp)
            .buttonHighlight(
                corner = smallCornerRadius
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
    matchNum: Int,
    time: Long,
    redAlliance: List<Int>,
    blueAlliance: List<Int>,
    showHighlight: Boolean,
    onCommentClicked: () -> Unit
) {
    val borderColor = if (showHighlight) Accent else Border
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .background(DarkGunmetal, shape = RoundedCornerShape(mediumCornerRadius))
            .border(1.dp, borderColor, shape = RoundedCornerShape(mediumCornerRadius))
            .buttonHighlight(
                corner = mediumCornerRadius
            )
            .padding(horizontal = 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(smallCornerRadius))
                .background(DarkishGunmetal)
                .buttonHighlight(
                    corner = smallCornerRadius
                )
                .width(135.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(matchNum.toString(), color = Deselected, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = R.drawable.clock),
                contentDescription = "time",
                colorFilter = ColorFilter.tint(Deselected),
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(time.convertUnixToMilitaryTime(), color = Deselected, fontSize = 16.sp)
        }


        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (t in redAlliance) {
                TeamNumber(t.toString())
            }
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
            for (t in blueAlliance) {
                TeamNumber(t.toString())
            }
        }



        Spacer(modifier = Modifier.weight(1f))

        Pressable (
            onClick = {onCommentClicked()},
            corner = smallCornerRadius,
            text = "Comment",
            modifier = Modifier
                .fillMaxHeight()
                .width(135.dp)
        ) {
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

fun calculateHighlight(matches: List<Match>, time: Long): Match {
    var l = 0
    var r = matches.size - 1

    while (l < r) {
        val mid = (l+r)/2
        val match_t = matches[mid].time.toLong()

        if (match_t <= time && (time-match_t) < 9.minutes.toLong(DurationUnit.MILLISECONDS)) {
            return matches[mid]
        }

        if (match_t < time) {
            l = mid + 1
        } else {
            r = mid - 1
        }
    }

    return matches[l]
}
fun showMatchHighlight(m: Match): Boolean {
    return m.redAlliance.any{it == 695} || m.blueAlliance.any{it == 695}
}