package com.team695.scoutifyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.team695.scoutifyapp.data.api.ScoutifyClient
import com.team695.scoutifyapp.data.api.service.MatchService
import com.team695.scoutifyapp.ui.theme.ScoutifyTheme
import com.team695.scoutifyapp.ui.viewModels.TaskService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val taskService = TaskService()
        val matchService: MatchService = ScoutifyClient.matchService

        setContent {
            ScoutifyTheme {
                Root(
                    taskService = taskService,
                    matchService = matchService
                    )
            }
        }
    }
}
