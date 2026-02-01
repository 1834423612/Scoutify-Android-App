package com.team695.scoutifyapp.ui.components.app.structure

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.team695.scoutifyapp.navigation.AppNav

@Composable
fun MainContent(modifier: Modifier = Modifier, navController: NavHostController) {
    Row(
        modifier = modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.weight(0.67f)) {
            AppNav(navController = navController)
        }
    }
}
