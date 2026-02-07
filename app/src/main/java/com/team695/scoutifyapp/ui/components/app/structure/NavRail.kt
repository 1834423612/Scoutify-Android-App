package com.team695.scoutifyapp.ui.components.app.structure

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team695.scoutifyapp.R
import com.team695.scoutifyapp.ui.theme.Accent
import com.team695.scoutifyapp.ui.theme.BackgroundTertiary
import com.team695.scoutifyapp.ui.theme.Deselected
import com.team695.scoutifyapp.ui.theme.Gunmetal
import com.team695.scoutifyapp.ui.theme.LightGunmetal
import com.team695.scoutifyapp.ui.theme.TextPrimary
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState



@Composable
fun NavRail(

onNavigateToHome: () -> Unit = {},
    onNavigateToPitScouting: () -> Unit = {},
    onNavigateToUpload: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    navController: NavHostController
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


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
        ImageBackground(x = 350f, y = 325f)
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
                        .width(140.dp)
                        .graphicsLayer {
                            translationX = -80f
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
                                translationX = 80f
                                translationY = 0f
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        NavItem(
                            icon = R.drawable.home,
                            label = "Home",
                            selected = currentRoute == "home",
                            onClick = {
                                onNavigateToHome()
                            }
                        )
                        NavItem(
                            icon = R.drawable.pits,
                            label = "Pit Scout",
                            selected = currentRoute == "pitScouting",
                            onClick = {
                                onNavigateToPitScouting()
                            }
                        )
                        NavItem(
                            icon = R.drawable.upload,
                            label = "Upload",
                            selected = currentRoute == "upload",
                            onClick = {
                                onNavigateToUpload()
                            }
                        )
                        NavItem(
                            icon = R.drawable.settings,
                            label = "Settings",
                            selected = currentRoute == "settings",
                            onClick = {
                                onNavigateToSettings()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NavItem(icon: Int, label: String, onClick: () -> Unit = {}, selected: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable{ onClick() }
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

