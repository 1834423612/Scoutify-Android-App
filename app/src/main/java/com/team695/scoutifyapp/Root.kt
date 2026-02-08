package com.team695.scoutifyapp

import android.app.Service
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.team695.scoutifyapp.data.api.service.ServiceInterface
import com.team695.scoutifyapp.navigation.AppNav
import com.team695.scoutifyapp.ui.components.app.structure.NavRail
import com.team695.scoutifyapp.ui.viewModels.ViewModelFactory
import com.team695.scoutifyapp.ui.viewModels.TaskService
import com.team695.scoutifyapp.ui.viewModels.TasksViewModel
import com.team695.scoutifyapp.ui.theme.*

@Composable
fun Root() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        if (LocalInspectionMode.current) {
            Box(modifier = Modifier.fillMaxSize()) {}
            return@Surface
        }
        // creators are the lambdas that make the viewmodel instance
        val owner = LocalViewModelStoreOwner.current
            ?: throw IllegalStateException("Root must be attached to a ViewModelStoreOwner")

        val viewModelMap: Map<String, ViewModel> = remember(owner) {
            val viewModelCreators: Map<String, () -> ViewModel> = mapOf(
                "home" to {
                    TasksViewModel(TaskService())
                },
            )
            // goes through all the pages and uses a viewModelProvider to return the viewmodel
            viewModelCreators.mapValues { (vmId, _) ->
                val factory = ViewModelFactory<ViewModel>(vmId, viewModelCreators)
                ViewModelProvider(owner, factory).get(ViewModel::class.java)
            }
        }

        val navController: NavHostController = rememberNavController()

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            NavRail(
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToPitScouting = { navController.navigate(route="pitScouting") },
                onNavigateToUpload = { navController.navigate(route="upload") },
                onNavigateToSettings = { navController.navigate(route="settings") },
                navController = navController
            )

            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(0.67f)) {
                    AppNav(navController = navController, viewModelMap = viewModelMap)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
fun RootPreview() {
    ScoutifyTheme {
        Root()
    }
}
