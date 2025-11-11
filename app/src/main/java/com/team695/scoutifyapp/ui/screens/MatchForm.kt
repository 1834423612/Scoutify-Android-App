package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.components.RB
import com.team695.scoutifyapp.ui.components.CB
import com.team695.scoutifyapp.ui.components.Counter
import com.team695.scoutifyapp.ui.components.Required
import com.team695.scoutifyapp.ui.components.TA
import com.team695.scoutifyapp.ui.components.divider
import Render

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchForm(back: () -> Unit, home: ()-> Unit) {
    Scaffold{ padding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            val requiredList = mutableListOf<Required>()

            fun <T> MutableList<T>.push(item: T): T {
                this.add(item)
                return item
            }

            val numberPattern = Regex("^\\d+(\\.\\d+)?$")

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) { Text("Comments") }

            Text("Match Q1 - Red 1" )
            Text("2603 - Steel Stingers", color = Color.Red)
            divider("Pregame")

            var preloaded by remember { mutableStateOf(listOf(false)) }
            CB(
                options =  listOf("Preloaded"),
                checkedStates = preloaded,
                onCheckedChange = { index, isChecked ->
                    preloaded = preloaded.toMutableList().also { it[index] = isChecked }
                },
            )

            divider("Autonomous")

            var _autonMobility by remember { mutableStateOf(listOf(false)) }
            CB(
                options =  listOf("Leaves Starting Zone"),
                checkedStates = _autonMobility,
                onCheckedChange = { index, isChecked ->
                    _autonMobility = _autonMobility.toMutableList().also { it[index] = isChecked }
                },
            )

            Render()

            divider("Teleoperated")

            var algaeDisloded by remember { mutableStateOf(0) }
            Counter("Algae Dislodged", algaeDisloded, onCountChange = { algaeDisloded = it })

            var algaeInOpponentProcessor by remember { mutableStateOf(0) }
            Counter("Algae in Opposite Alliance Processor", algaeInOpponentProcessor, onCountChange = { algaeInOpponentProcessor = it })

            var HPscored by remember { mutableStateOf(0) }
            Counter("Human Player Scored in Net", HPscored, onCountChange = { HPscored = it })

            var HPmissed by remember { mutableStateOf(0) }
            Counter("Human Player Shots Missed", HPmissed, onCountChange = { HPmissed = it })

            divider("Endgame")

            var _endgame by remember { mutableStateOf("") }
            RB(
                options = listOf("Robot Not Parked","Robot Parked","Robot Shallow Climb", "Robot Deep CLimb", "Robot Failed Climb"),
                selectedOption = _endgame,
                onOptionSelected = {_endgame = it },
                label = "Robot Endgame",
                required=requiredList.push(
                    remember { Required(valid = { _endgame!=="" }) }
                )
            )
            var climbPosition by remember { mutableStateOf("") }
            RB(
                options = listOf("Outer (Towards Processor)", "Middle","Inner (Away from Processor)","N/A"),
                selectedOption = climbPosition,
                onOptionSelected = {climbPosition = it },
                label = "Climb Position:",
                required=requiredList.push(
                    remember { Required(valid = { climbPosition!=="" }) }
                )
            )

            divider("Fouls")
            var fouls by remember { mutableStateOf(0) }
            Counter("Teleop + Endgame - Fouls", fouls, onCountChange = { fouls = it })
            var techFouls by remember { mutableStateOf(0) }
            Counter("Teleop + Endgame - Tech Fouls", techFouls, onCountChange = { techFouls = it })

            var enterOpponentBarge by remember { mutableStateOf(listOf(false)) }
            CB(
                options =  listOf("Robot entered the barge area"),
                checkedStates = enterOpponentBarge,
                onCheckedChange = { index, isChecked ->
                    enterOpponentBarge = enterOpponentBarge.toMutableList().also { it[index] = isChecked }
                },
            )

            var defense by remember { mutableStateOf(listOf(false)) }
            CB(
                options =  listOf("Robot plays defense"),
                checkedStates = defense,
                onCheckedChange = { index, isChecked ->
                    defense = defense.toMutableList().also { it[index] = isChecked }
                },
            )
            var groundCoralIntake by remember { mutableStateOf(listOf(false)) }
            CB(
                options =  listOf("Robot has coral ground intake"),
                checkedStates = groundCoralIntake,
                onCheckedChange = { index, isChecked ->
                    groundCoralIntake = groundCoralIntake.toMutableList().also { it[index] = isChecked }
                },
            )
            var groundAlgaeIntake by remember { mutableStateOf(listOf(false)) }
            CB(
                options =  listOf("Robot has coral ground intake"),
                checkedStates = groundAlgaeIntake,
                onCheckedChange = { index, isChecked ->
                    groundAlgaeIntake = groundAlgaeIntake.toMutableList().also { it[index] = isChecked }
                },
            )

            var note by remember { mutableStateOf("") }
            TA(
                label = "Comments", input = note, onChange = {note=it}
            )

            fun ColumnScope.onSubmit(): () -> Unit {
                return{
                    //combination of all constraints here
                    val valid = requiredList.all { it.valid() }

                    if(valid){
                        //! submit logic here
                        //just local storage for now
                    }else{
                        //make validation messages appear
                        for(i in requiredList){
                            i.focusLeftYet=true
                        }
                    }
                }
            }
            Button(
                onClick = onSubmit(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Save")
            }
            Text("If robot broke, you will have to file an incident report.")
            Button(
                onClick = {
                    //! schedule an incident report on task to do list (don't let user have multiple reports per match)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Schedule Incident Report")
            }
        }
    }
}
