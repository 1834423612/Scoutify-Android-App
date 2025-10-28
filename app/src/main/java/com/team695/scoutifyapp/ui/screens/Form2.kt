package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.team695.scoutifyapp.ui.components.InfoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Form2(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Scoutify App") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            InfoCard(
                title = "Test",
                description = "form2"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onBack) {
                Text("Back")
            }
        }
    }
}
