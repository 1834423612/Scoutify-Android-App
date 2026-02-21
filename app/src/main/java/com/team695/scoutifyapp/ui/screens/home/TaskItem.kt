package com.team695.scoutifyapp.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.team695.scoutifyapp.R
import com.team695.scoutifyapp.data.api.model.Task
import com.team695.scoutifyapp.ui.components.buttonHighlight
import com.team695.scoutifyapp.ui.components.progressBorder
import com.team695.scoutifyapp.ui.reusables.Pressable
import com.team695.scoutifyapp.ui.theme.DarkGunmetal
import com.team695.scoutifyapp.ui.theme.DarkishGunmetal
import com.team695.scoutifyapp.ui.theme.Deselected
import com.team695.scoutifyapp.ui.theme.mediumCornerRadius
import com.team695.scoutifyapp.ui.theme.smallCornerRadius

@Composable
fun TaskItem(task: Task, onPress: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .progressBorder(progress=task.progress)
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
            Text(task.time.toString(), color = Deselected)
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