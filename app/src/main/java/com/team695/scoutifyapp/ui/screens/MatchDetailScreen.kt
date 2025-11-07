package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class) // Add this annotation
@Composable
fun MatchDetailScreen(
    matchNumber: String,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Auto", "Teleop", "Notes")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Match $matchNumber Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> OverviewTab()
                1 -> AutoTab()
                2 -> TeleopTab()
                3 -> NotesTab()
            }
        }
    }
}

// 以下 Tab 内容复用前面 InfoCard 示例
@Composable private fun OverviewTab() { /*...同前代码...*/ }
@Composable private fun AutoTab() { /*...同前代码...*/ }
@Composable private fun TeleopTab() { /*...同前代码...*/ }
@Composable private fun NotesTab() { /*...同前代码...*/ }
@Composable private fun InfoCard(title: String, value: String) { /*...同前代码...*/ }
