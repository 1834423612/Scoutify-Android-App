package com.team695.scoutifyapp.ui.components.form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team695.scoutifyapp.ui.theme.*

data class Task(
    val id: Int,
    val teamNumber: String,
    val event: String,
    val timeElapsed: String,
    val progress: Int,
    val status: TaskStatus = TaskStatus.IN_PROGRESS
)

enum class TaskStatus {
    IN_PROGRESS,
    INCOMPLETE,
    DONE
}

@Composable
fun TasksSidebar(
    tasks: List<Task> = emptyList(),
    selectedTaskId: Int? = null,
    onTaskSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(BgSecondary)
            .padding(16.dp)
    ) {
        // Header
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Pit Scouting",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            AccentPrimary.copy(alpha = 0.2f),
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            AccentPrimary,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "3",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentPrimary
                            )
                        )
                        Text(
                            text = "In Prog",
                            style = TextStyle(
                                fontSize = 9.sp,
                                color = AccentPrimary
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            BgCard,
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            BorderColor,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "5",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        )
                        Text(
                            text = "Incomp",
                            style = TextStyle(
                                fontSize = 9.sp,
                                color = TextSecondary
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            BgCard,
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            BorderColor,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "12",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        )
                        Text(
                            text = "Done",
                            style = TextStyle(
                                fontSize = 9.sp,
                                color = TextSecondary
                            )
                        )
                    }
                }
            }
        }

        // Tasks List
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    isSelected = task.id == selectedTaskId,
                    onClick = { onTaskSelected(task.id) }
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (isSelected) BgTertiary else BgCard,
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                if (isSelected) AccentPrimary else Border,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Team ${task.teamNumber}",
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            )

            Box(
                modifier = Modifier
                    .background(
                        when (task.status) {
                            TaskStatus.IN_PROGRESS -> AccentWarning.copy(alpha = 0.2f)
                            TaskStatus.INCOMPLETE -> AccentDanger.copy(alpha = 0.2f)
                            TaskStatus.DONE -> AccentSecondary.copy(alpha = 0.2f)
                        },
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = when (task.status) {
                        TaskStatus.IN_PROGRESS -> "⏱"
                        TaskStatus.INCOMPLETE -> "⚠"
                        TaskStatus.DONE -> "✓"
                    },
                    style = TextStyle(
                        fontSize = 10.sp,
                        color = when (task.status) {
                            TaskStatus.IN_PROGRESS -> AccentWarning
                            TaskStatus.INCOMPLETE -> AccentDanger
                            TaskStatus.DONE -> AccentSecondary
                        }
                    )
                )
            }
        }

        Text(
            text = task.event,
            style = TextStyle(
                fontSize = 11.sp,
                color = TextSecondary
            )
        )

        Text(
            text = "⏱️ ${task.timeElapsed}",
            style = TextStyle(
                fontSize = 11.sp,
                color = TextMuted
            )
        )
    }
}
