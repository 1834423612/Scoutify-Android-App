package com.team695.scoutifyapp.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.KeyboardType
import com.example.ui.components.RB
import com.team695.scoutifyapp.ui.components.CB
import com.team695.scoutifyapp.ui.components.ImagePicker
import com.team695.scoutifyapp.ui.components.OTF
import com.team695.scoutifyapp.ui.components.Required
import com.team695.scoutifyapp.ui.components.TA


@OptIn(ExperimentalMaterial3Api::class)
@Composable
//!todo: clicking outside of a text input should remove focus,
// scrollbar visible?,
// pictures
//submit -> do submit backend
//image upload
//canvas functionality
//turn required into an array/object (i.e. the focusedLeftYet)


// required/validation, - for text: use Regex("^.+$"), radio has an example
fun PitForm(back: () -> Unit,home:()->Unit) {
    Scaffold{ padding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            var requiredList = mutableListOf<Required>()

            fun <T> MutableList<T>.push(item: T): T {
                this.add(item)
                return item
            }


            val numberPattern = Regex("^\\d+(\\.\\d+)?$")

//            var teamName by remember { mutableStateOf("") }
//            OTF(
//                value = teamName,
//                onChange = { teamName = it },
//                title = "Enter the Team Number"
//            )
//
//            Text("Type of Drive Train")
//            var _drive by remember { mutableStateOf("") }
//            val drive = listOf("Tank Drive (\"skid steer\", plates on both sides of wheels)", "West Coast Drive (wheels mounted off one side of tube)", "Swerve Drive", "Other")
//            var otherDrive by remember { mutableStateOf("") }
//            RB(
//                options = drive,
//                selectedOption = _drive,
//                onOptionSelected = {_drive = it },
//                otherText = otherDrive,
//                onOtherTextChange = { otherDrive = it },
//                label = "Select the type of drive train used in your robot design.",
//                required=requiredList.push(Required(focusStarted = false, focusLeftYet =  false, valid={_drive!==""} ))
//            )
//
//            Text("Type of Wheels Used")
//            var _wheels by remember { mutableStateOf("") }
//            val wheels = listOf("Traction", "Mecanum (rollers at 45° angle)", "Omni (rollers at 90° angle)", "Other")
//            var otherWheels by remember { mutableStateOf("") }
//            RB(
//                options = wheels,
//                selectedOption = _wheels,
//                onOptionSelected = {_wheels = it },
//                otherText = otherWheels,
//                onOtherTextChange = { otherWheels = it },
//                label = "Choose the type of wheels used on your robot.",
//                required=requiredList.push(Required(focusStarted = false, focusLeftYet =  false, valid={_wheels!==""}))
//            )
//
//            val intakes = listOf("Ground", "Feeder Station", "Other")
//            var _intakes by remember { mutableStateOf(listOf(false, false, false)) }
//            var otherIntakes by remember { mutableStateOf("") }
//            CB(
//                label = "Intakes:",
//                options = intakes,
//                checkedStates = _intakes,
//                onCheckedChange = { index, isChecked ->
//                    _intakes = _intakes.toMutableList().also { it[index] = isChecked }
//                },
//                otherText = otherIntakes,
//                onOtherTextChange = { otherIntakes = it }
//            )

            var _coralAcquisition by remember { mutableStateOf("") }
            val coralAcquisition = listOf("None", "Coral Station Only", "Floor Only", "Coral Station and Floor")
            RB(
                options = coralAcquisition,
                selectedOption = _coralAcquisition,
                onOptionSelected = {_coralAcquisition = it },
                label = "Coral Acquisition(Scoring Method):",
                required=requiredList.push(Required(valid={_coralAcquisition!==""}))
            )

//            val scoringLocations = listOf("L1", "L2", "L3","L4","Algae in Processor","Algae in Net")
//            var _scoringLocations by remember { mutableStateOf(listOf(false, false, false,false,false,false)) }
//            CB(
//                label = "Scoring Locations:",
//                options = scoringLocations,
//                checkedStates = _scoringLocations,
//                onCheckedChange = { index, isChecked ->
//                    _scoringLocations = _scoringLocations.toMutableList().also { it[index] = isChecked }
//                },
//            )
//
//            var _algaeAcquisition by remember { mutableStateOf("") }
//            val algaeAcquisition = listOf("None", "Reef Only", "Floor Only", "Reef and Floor")
//            RB(
//                options = algaeAcquisition,
//                selectedOption = _algaeAcquisition,
//                onOptionSelected = {_algaeAcquisition = it },
//                label = "Algae Acquisition(Scoring Method):",
//                required=requiredList.push(Required(focusStarted = false, focusLeftYet =  false, valid={_algaeAcquisition!==""}))
//            )
//
//            var _algaeScoring by remember { mutableStateOf("") }
//            val algaeScoring = listOf("None", "Processor Only", "Net Only", "Processor and Net")
//            RB(
//                options = algaeScoring,
//                selectedOption = _algaeScoring,
//                onOptionSelected = {_algaeScoring = it },
//                label = "Algae Scoring:",
//                required=requiredList.push(Required(focusStarted = false, focusLeftYet = false, valid={_algaeScoring!==""}))
//            )
//
//            val climb = listOf("Deep Climb", "Shallow Climb")
//            var _climb by remember { mutableStateOf(listOf(false, false)) }
//            CB(
//                label = "Cage Climbing:",
//                options = climb,
//                checkedStates = _climb,
//                onCheckedChange = { index, isChecked ->
//                    _climb = _climb.toMutableList().also { it[index] = isChecked }
//                },
//            )
//
//            val autoMove = listOf("Yes")
//            var _autoMove by remember { mutableStateOf(listOf(false)) }
//            CB(
//                label = "Robot leaves their Starting Zone during autonomous?",
//                options = autoMove,
//                checkedStates = _autoMove,
//                onCheckedChange = { index, isChecked ->
//                    _autoMove = _autoMove.toMutableList().also { it[index] = isChecked }
//                },
//            )

            Text("Robot Weight (without Bumpers)")
            var weight by remember { mutableStateOf("") }
            val _required1 = remember { Required(valid = { weight !== "" && weight.matches(numberPattern) }) }
            requiredList.add(_required1)
            OTF(
                title = "Enter the weight of the robot in pounds.",
                value = weight,
                onChange = { weight = it },
                keyboardType = KeyboardType.Number,
                required=_required1
            )

            Text("Robot Length (without Bumpers)")
            var length by remember { mutableStateOf("") }
            val _reguired0 = remember {  Required(valid={length!==""&&length.matches(numberPattern)}) }
            requiredList.add(_reguired0)
            OTF(
                title = "Enter the length of the robot in inches without bumpers(front to back).",
                value = length,
                onChange = { length = it },
                keyboardType = KeyboardType.Number,
                required=_reguired0
            )

//            Text("Robot Width (without Bumpers)")
//            var width by remember { mutableStateOf("") }
//            OTF(
//                title = "Enter the width of the robot in inches without bumpers(left to right).",
//                value = width,
//                onChange = { width = it },
//                keyboardType = KeyboardType.Number,
//                required=requiredList.push(Required(valid={width!==""&&width.matches(numberPattern)},focusStarted = false, focusLeftYet =  false))
//            )
//
//            Text("Robot Height")
//            var height by remember { mutableStateOf("") }
//            OTF(
//                title = "Enter the height of the robot in inches from the floor to the highest point on the robot at the start of the match.",
//                value = height,
//                onChange = { height = it },
//                keyboardType = KeyboardType.Number,
//                required=requiredList.push(Required(focusStarted = false, focusLeftYet =  false, valid={height!==""&&height.matches(numberPattern)} ))
//            )
//
//            Text("Robot Height When Fully Extended")
//            var heightMax by remember { mutableStateOf("") }
//            OTF(
//                title = "Enter the height of the robot in inches from the floor to the highest point on the robot at the start of the match.",
//                value = heightMax,
//                onChange = { heightMax = it },
//                keyboardType = KeyboardType.Number,
//                required=requiredList.push(Required(focusStarted = false, focusLeftYet =  false, valid={heightMax!==""&&heightMax.matches(numberPattern)}))
//            )
//
//            var _driveTeam by remember { mutableStateOf("") }
//            val driveTeam = listOf("One person driving and operating the robot during a match", "Other")
//            var otherDriveTeam by remember { mutableStateOf("") }
//            RB(
//                options = driveTeam,
//                selectedOption = _driveTeam,
//                onOptionSelected = {_driveTeam = it },
//                otherText = otherDriveTeam,
//                onOtherTextChange = { otherDriveTeam = it },
//                label = "Drive Team Members",
//                required=requiredList.push(Required(focusStarted = false, focusLeftYet =  false,valid={_driveTeam!==""}))
//            )
//
//            var practice by remember { mutableStateOf("") }
//            OTF(
//                value = practice,
//                onChange = { practice = it },
//                title = "Hours/Weeks of Practice"
//            )
//
//            var note by remember { mutableStateOf("") }
//            TA(
//                label = "Additional Comments", input = note, onChange = {note=it}
//            )
//
////            var file0 by remember { mutableStateOf<Uri?>(null) }
////            Text("Full Robot Image")
////            ImagePicker(
////                onImageSelected = { uri -> file0 = uri }
////            )
////
////            var file1 by remember { mutableStateOf<Uri?>(null) }
////            Text("Drive Train Image")
////            ImagePicker(
////                onImageSelected = { uri -> file1 = uri }
////            )

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
                        }//.forEach { it.focusLeftYet=true }
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
        }
    }
}