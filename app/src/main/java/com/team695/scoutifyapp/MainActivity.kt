package com.team695.scoutifyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.team695.scoutifyapp.ui.theme.ScoutifyTheme
import com.team695.scoutifyapp.ui.viewModels.TaskService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val taskService: TaskService = TaskService()

        setContent {
            ScoutifyTheme {
                Root(taskService = taskService)
            }
        }
    }
}
