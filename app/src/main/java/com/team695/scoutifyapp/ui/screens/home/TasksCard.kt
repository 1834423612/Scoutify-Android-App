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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.team695.scoutifyapp.R
import com.team695.scoutifyapp.data.api.model.Task
import com.team695.scoutifyapp.data.api.model.TaskType
import com.team695.scoutifyapp.ui.components.progressBorder
import com.team695.scoutifyapp.ui.reusables.Pressable
import com.team695.scoutifyapp.ui.components.BackgroundGradient
import com.team695.scoutifyapp.ui.components.ImageBackground
import com.team695.scoutifyapp.ui.components.buttonHighlight
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
    onPress: (taskId: Int) -> Unit,
) {
    val tabState by homeViewModel.tabState.collectAsStateWithLifecycle()
    val tabs = arrayOf("Incomplete", "Complete")

    val tasksState by homeViewModel.tasksState.collectAsStateWithLifecycle()
    val incompleteTasks = tasksState?.filter { it -> !it.isDone }
    val completeTasks = tasksState?.filter { it -> it.isDone }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
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
                    selectedTabIndex = tabState.selectedTab,
                    containerColor = Color.Transparent,
                    indicator = {},
                    divider = {},
                    modifier = Modifier
                        .padding(all = 6.dp)

                ) {
                    tabs.forEachIndexed { index, tabTitle ->
                        Tab(
                            selected = tabState.selectedTab == index,
                            onClick = {homeViewModel.selectTab(index = index)},
                            modifier = if (tabState.selectedTab == index) Modifier
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
                                            translationX = 0F
                                            translationY = 0F
                                        }
                                ) {
                                    val text = (if (index==0) incompleteTasks?.size
                                        else completeTasks?.size)
                                            .toString()

                                    Text(
                                        text=text, color = BadgeContent)
                                }
                            }
                        }
                    }


                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val tasks = (if (tabState.selectedTab == 0) incompleteTasks else completeTasks)
                    ?.sorted()

                if(tasks != null) {
                    items(tasks) { task ->
                        TaskItem(task = task, onPress = {onPress.invoke(task.id)})
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 200)
@Composable
fun TaskItemPreview() {
    val dummyTask: Task = Task(id=0, type = TaskType.SCOUTING, matchNum = 0, teamNum = "test", time = 0L, progress = 0f, isDone = false)

    TaskItem(task = dummyTask, onPress = {})
}