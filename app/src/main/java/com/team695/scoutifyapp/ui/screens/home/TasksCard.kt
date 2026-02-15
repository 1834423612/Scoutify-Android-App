package com.team695.scoutifyapp.ui.screens.home

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.team695.scoutifyapp.R
import com.team695.scoutifyapp.data.api.model.Task
import com.team695.scoutifyapp.ui.reusables.Pressable
import com.team695.scoutifyapp.ui.reusables.BackgroundGradient
import com.team695.scoutifyapp.ui.reusables.ImageBackground
import com.team695.scoutifyapp.ui.modifier.buttonHighlight
import com.team695.scoutifyapp.ui.theme.BadgeBackground
import com.team695.scoutifyapp.ui.theme.BadgeContent
import com.team695.scoutifyapp.ui.theme.Background
import com.team695.scoutifyapp.ui.theme.DarkGunmetal
import com.team695.scoutifyapp.ui.theme.DarkishGunmetal
import com.team695.scoutifyapp.ui.theme.Deselected
import com.team695.scoutifyapp.ui.theme.Gunmetal
import com.team695.scoutifyapp.ui.theme.LightGunmetal
import com.team695.scoutifyapp.ui.theme.ProgressGreen
import com.team695.scoutifyapp.ui.theme.TextPrimary
import com.team695.scoutifyapp.ui.theme.mediumCornerRadius
import com.team695.scoutifyapp.ui.theme.smallCornerRadius
import com.team695.scoutifyapp.ui.viewModels.HomeViewModel

@Composable
fun TasksCard(
    homeViewModel: HomeViewModel,
    onPress: () -> Unit,
) {

    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val tabs = arrayOf("Incomplete", "Complete")

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(smallCornerRadius))

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
                    .border(1.dp, LightGunmetal, RoundedCornerShape(smallCornerRadius))
                    .background(Background)
            ) {
                PrimaryTabRow(
                    selectedTabIndex = uiState.selectedTab,
                    containerColor = Color.Transparent,
                    indicator = {},
                    divider = {},
                    modifier = Modifier
                        .padding(all = 6.dp)

                ) {
                    tabs.forEachIndexed { index, tabTitle ->
                        Tab(
                            selected = uiState.selectedTab == index,
                            onClick = {homeViewModel.selectTab(index = index)},
                            modifier = if (uiState.selectedTab == index) Modifier
                                .background(
                                    Gunmetal,
                                    shape = RoundedCornerShape(smallCornerRadius)
                                )
                                .buttonHighlight(
                                    corner = smallCornerRadius
                                ) else Modifier.background(Color.Transparent),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(text=tabTitle, color = TextPrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Badge(
                                    containerColor = BadgeBackground,
                                    modifier = Modifier
                                        .graphicsLayer {
                                            translationX = -10f
                                            translationY = -10f
                                        }
                                ) { Text(text=if(index==0) uiState.incompleteTasks.size.toString() else uiState.completeTasks.size.toString() , color = BadgeContent) }
                            }
                        }
                    }


                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val tasks = if (uiState.selectedTab == 0) uiState.incompleteTasks else uiState.completeTasks
                items(tasks) { task ->
                    TaskItem(task = task, onPress = onPress)
                }
            }
        }
    }
}

fun Modifier.taskBorder(
    progress: Float
): Modifier {
    if(progress == 1f) {
        return this.then(
            other = Modifier
                .border(
                    width = 2f.dp,
                    color = ProgressGreen,
                    shape = RoundedCornerShape(mediumCornerRadius))
        )
    }
    else if (progress == 0f) {
        return this.then(
            other = Modifier
                .border(
                    width = 2f.dp,
                    color = DarkishGunmetal,
                    shape = RoundedCornerShape(mediumCornerRadius))
        )
    }
    return this.then(
        other = Modifier
            .border(
                width = 2f.dp,
                brush = borderGradient(progress),
                shape = RoundedCornerShape(mediumCornerRadius))
    )
}

fun borderGradient(progress: Float): Brush {
    return Brush.linearGradient(
        colorStops = arrayOf(
            0f to ProgressGreen,
            (progress-0.1f) to ProgressGreen,
            (progress+0.1f) to DarkGunmetal,
            1f to DarkishGunmetal,
        )
    )
}


@Composable
fun TaskItem(task: Task, onPress: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .taskBorder(progress=task.progress)
            .background(color = DarkGunmetal, shape = RoundedCornerShape(mediumCornerRadius))
            .clip(RoundedCornerShape(mediumCornerRadius))
            .buttonHighlight(
                corner = smallCornerRadius
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(smallCornerRadius))
                .background(DarkishGunmetal)
                .width(80.dp)
                .buttonHighlight(
                    corner = smallCornerRadius
                )
            ) {
            Image(
                painter = painterResource(id = R.drawable.edit),
                colorFilter = ColorFilter.tint(Deselected),
                contentDescription = "Edit",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Q${task.matchNum}", color = Deselected)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(smallCornerRadius))
                .background(DarkishGunmetal)
                .width(60.dp)
                .buttonHighlight(
                    corner = smallCornerRadius
                )
        ) {
            Text(task.teamNum, color = Deselected)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(smallCornerRadius))
                .background(DarkishGunmetal)
                .width(110.dp)
                .buttonHighlight(
                    corner = smallCornerRadius
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.clock),
                contentDescription = "Time",
                modifier = Modifier
                    .size(16.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(task.time, color = Deselected)
        }

        Pressable (
            onClick = {onPress()},
            corner = smallCornerRadius,
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