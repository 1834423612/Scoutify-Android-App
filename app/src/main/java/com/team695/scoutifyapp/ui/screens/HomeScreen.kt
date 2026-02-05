package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team695.scoutifyapp.ui.components.form.NavSidebar
import com.team695.scoutifyapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToForm: () -> Unit, 
    onNavigateToForm2: () -> Unit, 
    onNavigateToPitScouting: () -> Unit,
    onNavigate: (String) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        // Navigation Sidebar - 临时禁用来测试
        // NavSidebar(
        //     currentScreen = "home",
        //     onNavigate = onNavigate
        // )
        
        // Main Content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(BgPrimary)
        ) {
            CenterAlignedTopAppBar(
                title = { Text("Scoutify App") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BgSecondary
                )
            )
            
            // 最简单的测试按钮
            Button(
                onClick = { onNavigateToPitScouting() },
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Text("TEST BUTTON")
            }
            
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BgCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Team 695", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Welcome to our scouting app!", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            item {
                Text(
                    text = "Available Features",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                FeatureCard(
                    title = "Pit Scouting Form",
                    description = "Complete pit scouting data collection with robot measurements, images, and abilities tracking",
                    icon = "🔧",
                    onClick = onNavigateToPitScouting
                )
            }

            item {
                FeatureCard(
                    title = "Match Form",
                    description = "Track match performance and statistics in real-time",
                    icon = "📊",
                    onClick = onNavigateToForm
                )
            }

            item {
                FeatureCard(
                    title = "Form 2",
                    description = "Additional scouting and data collection features",
                    icon = "📋",
                    onClick = onNavigateToForm2
                )
            }
        } // LazyColumn
        } // Column
    } // Row
}

@Composable
fun FeatureCard(
    title: String,
    description: String,
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(BgCard, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                style = TextStyle(fontSize = 32.sp),
                modifier = Modifier.align(Alignment.Top)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = description,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = TextSecondary
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Navigate",
                tint = AccentPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
