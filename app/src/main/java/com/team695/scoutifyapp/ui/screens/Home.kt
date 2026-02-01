package com.team695.scoutifyapp.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.team695.scoutifyapp.ui.components.InfoCard
import com.team695.scoutifyapp.ui.components.Bison


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun Home(navigateToPitSchedule: () -> Unit, navigateToMatchSchedule:()->Unit) {
    Scaffold{ padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            InfoCard(
                title = "Team 695",
                description = "Welcome to our scouting app!"
            )

            Bison()


            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = navigateToPitSchedule) {
                Text("Pit Scouting Assignments")
            }

            Button(onClick = navigateToMatchSchedule) {
                Text("Match Scouting Assignments ")
            }

            Spacer(modifier = Modifier.height(8.dp))

        }
    }
}
