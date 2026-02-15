package com.team695.scoutifyapp.ui.screens.dataCollection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.team695.scoutifyapp.ui.screens.home.MatchSchedule
import com.team695.scoutifyapp.ui.screens.home.TasksCard
import com.team695.scoutifyapp.ui.viewModels.DataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataScreen(
    navController: NavHostController,
    dataViewModel: DataViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
    }
}