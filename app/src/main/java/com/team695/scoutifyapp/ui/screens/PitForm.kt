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
import com.team695.scoutifyapp.ui.components.TA


@OptIn(ExperimentalMaterial3Api::class)
@Composable
//!todo: clicking outside of a text input should remove focus,
// scrollbar visible?,
// pictures
//turn form into components
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
            val numberPattern = Regex("^\\d+(\\.\\d+)?$")

            var teamName by remember { mutableStateOf("") }
            OTF(
                value = teamName,
                onChange = { teamName = it },
                title = "Enter the Team Number"
            )

            Text("Type of Drive Train")
            var focusStarted0 by remember { mutableStateOf(false) }
            var focusedLeftYet0 by remember { mutableStateOf(false) }
            var _drive by remember { mutableStateOf("") }
            val drive = listOf("Tank Drive (\"skid steer\", plates on both sides of wheels)", "West Coast Drive (wheels mounted off one side of tube)", "Swerve Drive", "Other")
            var otherDrive by remember { mutableStateOf("") }
            RB(
                options = drive,
                selectedOption = _drive,
                onOptionSelected = {_drive = it },
                otherText = otherDrive,
                onOtherTextChange = { otherDrive = it },
                label = "Select the type of drive train used in your robot design.",
                focusedLeftYet = focusedLeftYet0,
                onFocusUpdate = { started ->
                    if (started) focusStarted0 = true
                    else if (focusStarted0) focusedLeftYet0 = true
                }
            )

            Text("Type of Wheels Used")
            var focusStarted1 by remember { mutableStateOf(false) }
            var focusedLeftYet1 by remember { mutableStateOf(false) }
            var _wheels by remember { mutableStateOf("") }
            val wheels = listOf("Traction", "Mecanum (rollers at 45° angle)", "Omni (rollers at 90° angle)", "Other")
            var otherWheels by remember { mutableStateOf("") }
            RB(
                options = wheels,
                selectedOption = _wheels,
                onOptionSelected = {_wheels = it },
                otherText = otherWheels,
                onOtherTextChange = { otherWheels = it },
                label = "Choose the type of wheels used on your robot.",
                focusedLeftYet = focusedLeftYet1,
                onFocusUpdate = { started ->
                    if (started) focusStarted1 = true
                    else if (focusStarted1) focusedLeftYet1 = true
                }
            )

            val intakes = listOf("Ground", "Feeder Station", "Other")
            var _intakes by remember { mutableStateOf(listOf(false, false, false)) }
            var otherIntakes by remember { mutableStateOf("") }
            CB(
                label = "Intakes:",
                options = intakes,
                checkedStates = _intakes,
                onCheckedChange = { index, isChecked ->
                    _intakes = _intakes.toMutableList().also { it[index] = isChecked }
                },
                otherText = otherIntakes,
                onOtherTextChange = { otherIntakes = it }
            )

            var focusStarted2 by remember { mutableStateOf(false) }
            var focusedLeftYet2 by remember { mutableStateOf(false) }
            var _coralAcquisition by remember { mutableStateOf("") }
            val coralAcquisition = listOf("None", "Coral Station Only", "Floor Only", "Coral Station and Floor")
            RB(
                options = coralAcquisition,
                selectedOption = _coralAcquisition,
                onOptionSelected = {_coralAcquisition = it },
                label = "Coral Acquisition(Scoring Method):",
                focusedLeftYet = focusedLeftYet2,
                onFocusUpdate = { started ->
                    if (started) focusStarted2 = true
                    else if (focusStarted2) focusedLeftYet2 = true
                }
            )

            val scoringLocations = listOf("L1", "L2", "L3","L4","Algae in Processor","Algae in Net")
            var _scoringLocations by remember { mutableStateOf(listOf(false, false, false,false,false,false)) }
            CB(
                label = "Scoring Locations:",
                options = scoringLocations,
                checkedStates = _scoringLocations,
                onCheckedChange = { index, isChecked ->
                    _scoringLocations = _scoringLocations.toMutableList().also { it[index] = isChecked }
                },
            )

            var focusStarted3 by remember { mutableStateOf(false) }
            var focusedLeftYet3 by remember { mutableStateOf(false) }
            var _algaeAcquisition by remember { mutableStateOf("") }
            val algaeAcquisition = listOf("None", "Reef Only", "Floor Only", "Reef and Floor")
            RB(
                options = algaeAcquisition,
                selectedOption = _algaeAcquisition,
                onOptionSelected = {_algaeAcquisition = it },
                label = "Algae Acquisition(Scoring Method):",
                focusedLeftYet = focusedLeftYet3,
                onFocusUpdate = { started ->
                    if (started) focusStarted3 = true
                    else if (focusStarted3) focusedLeftYet3 = true
                }
            )

            var focusStarted4 by remember { mutableStateOf(false) }
            var focusedLeftYet4 by remember { mutableStateOf(false) }
            var _algaeScoring by remember { mutableStateOf("") }
            val algaeScoring = listOf("None", "Processor Only", "Net Only", "Processor and Net")
            RB(
                options = algaeScoring,
                selectedOption = _algaeScoring,
                onOptionSelected = {_algaeScoring = it },
                label = "Algae Scoring:",
                focusedLeftYet = focusedLeftYet4,
                onFocusUpdate = { started ->
                    if (started) focusStarted4 = true
                    else if (focusStarted4) focusedLeftYet4 = true
                }
            )

            val climb = listOf("Deep Climb", "Shallow Climb")
            var _climb by remember { mutableStateOf(listOf(false, false)) }
            CB(
                label = "Cage Climbing:",
                options = climb,
                checkedStates = _climb,
                onCheckedChange = { index, isChecked ->
                    _climb = _climb.toMutableList().also { it[index] = isChecked }
                },
            )

            val autoMove = listOf("Yes")
            var _autoMove by remember { mutableStateOf(listOf(false)) }
            CB(
                label = "Robot leaves their Starting Zone during autonomous?",
                options = autoMove,
                checkedStates = _autoMove,
                onCheckedChange = { index, isChecked ->
                    _autoMove = _autoMove.toMutableList().also { it[index] = isChecked }
                },
            )

            Text("Robot Weight (without Bumpers)")
            var weight by remember { mutableStateOf("") }
            var focusedLeftYet5 by remember { mutableStateOf(false) }
            var focusStarted5 by remember { mutableStateOf(false) }
            OTF(
                title = "Enter the weight of the robot in pounds.",
                value = weight,
                onChange = { it->{weight = it }},
                keyboardType = KeyboardType.Number,
                pattern = numberPattern,
                focusedLeftYet = focusedLeftYet5,
                onFocusUpdate = { started ->
                    if (started) focusStarted5 = true
                    else if (focusStarted5) focusedLeftYet5 = true
                }
            )

            Text("Robot Length (without Bumpers)")
            var length by remember { mutableStateOf("") }
            var focusedLeftYet6 by remember { mutableStateOf(false) }
            var focusStarted6 by remember { mutableStateOf(false) }
            OTF(
                title = "Enter the length of the robot in inches without bumpers(front to back).",
                value = length,
                onChange = { length = it },
                keyboardType = KeyboardType.Number,
                pattern = numberPattern,
                focusedLeftYet = focusedLeftYet6,
                onFocusUpdate = { started ->
                    if (started) focusStarted6 = true
                    else if (focusStarted6) focusedLeftYet6 = true
                }
            )

            Text("Robot Width (without Bumpers)")
            var width by remember { mutableStateOf("") }
            var focusedLeftYet7 by remember { mutableStateOf(false) }
            var focusStarted7 by remember { mutableStateOf(false) }
            OTF(
                title = "Enter the width of the robot in inches without bumpers(left to right).",
                value = width,
                onChange = { width = it },
                keyboardType = KeyboardType.Number,
                pattern = numberPattern,
                focusedLeftYet = focusedLeftYet7,
                onFocusUpdate = { started ->
                    if (started) focusStarted7 = true
                    else if (focusStarted7) focusedLeftYet7 = true
                }
            )

            Text("Robot Height")
            var height by remember { mutableStateOf("") }
            var focusedLeftYet8 by remember { mutableStateOf(false) }
            var focusStarted8 by remember { mutableStateOf(false) }
            OTF(
                title = "Enter the height of the robot in inches from the floor to the highest point on the robot at the start of the match.",
                value = height,
                onChange = { height = it },
                keyboardType = KeyboardType.Number,
                pattern = numberPattern,
                focusedLeftYet = focusedLeftYet8,
                onFocusUpdate = { started ->
                    if (started) focusStarted8 = true
                    else if (focusStarted8) focusedLeftYet8 = true
                }
            )

            Text("Robot Height When Fully Extended")
            var heightMax by remember { mutableStateOf("") }
            var focusedLeftYet9 by remember { mutableStateOf(false) }
            var focusStarted9 by remember { mutableStateOf(false) }
            OTF(
                title = "Enter the height of the robot in inches from the floor to the highest point on the robot at the start of the match.",
                value = heightMax,
                onChange = { heightMax = it },
                keyboardType = KeyboardType.Number,
                pattern = numberPattern,
                focusedLeftYet = focusedLeftYet9,
                onFocusUpdate = { started ->
                    if (started) focusStarted9 = true
                    else if (focusStarted9) focusedLeftYet9 = true
                }
            )

            var focusStarted10 by remember { mutableStateOf(false) }
            var focusedLeftYet10 by remember { mutableStateOf(false) }
            var _driveTeam by remember { mutableStateOf("") }
            val driveTeam = listOf("One person driving and operating the robot during a match", "Other")
            var otherDriveTeam by remember { mutableStateOf("") }
            RB(
                options = driveTeam,
                selectedOption = _driveTeam,
                onOptionSelected = {_driveTeam = it },
                otherText = otherDriveTeam,
                onOtherTextChange = { otherDriveTeam = it },
                label = "Drive Team Members",
                focusedLeftYet = focusedLeftYet10,
                onFocusUpdate = { started ->
                    if (started) focusStarted10 = true
                    else if (focusStarted10) focusedLeftYet10 = true
                }
            )

            var practice by remember { mutableStateOf("") }
            OTF(
                value = practice,
                onChange = { practice = it },
                title = "Hours/Weeks of Practice"
            )


//            val checkboxOptionsLabels0 = listOf("Option 1", "Option 2", "Other")
//            var checkboxOptionsBool0 by remember { mutableStateOf(listOf(false, false, false)) }
//            var otherText0 by remember { mutableStateOf("") }
//            CB(
//                label = "Choose options",
//                options = checkboxOptionsLabels0,
//                checkedStates = checkboxOptionsBool0,
//                onCheckedChange = { index, isChecked ->
//                    checkboxOptionsBool0 = checkboxOptionsBool0.toMutableList().also { it[index] = isChecked }
//                },
//                otherText = otherText0,
//                onOtherTextChange = { otherText0 = it }
//            )
//
//            val checkLabels = listOf("Option 1", "Option 2", "Other")
//            var checkBools by remember { mutableStateOf(listOf(false, false, false)) }
//            CB(
//                label = "Choose options",
//                options = checkLabels,
//                checkedStates = checkBools,
//                onCheckedChange = { index, isChecked ->
//                    checkBools = checkBools.toMutableList().also { it[index] = isChecked }
//                },
//            )
//
//            var number0 by remember { mutableStateOf("") }
//            OTF(
//                label = "number",
//                title = "Number Keyboard",
//                value=number0,
//                onChange = { number0 = it },
//                keyboardType = KeyboardType.Number
//            )
//
//
//            var number1 by remember { mutableStateOf("") }
//            val pattern0 = Regex("^\\d+$")
//            var focusedLeftYet0 by remember { mutableStateOf(false) }
//            var focusStarted0 by remember { mutableStateOf(false) }
//            OTF(
//                title = "Enter a number",
//                value = number1,
//                onChange = { number1 = it },
//                keyboardType = KeyboardType.Number,
//                pattern = pattern0,
//                focusedLeftYet = focusedLeftYet0,
//                onFocusUpdate = { started ->
//                    if (started) focusStarted0 = true
//                    else if (focusStarted0) focusedLeftYet0 = true
//                }
//            )
//
//            var focusStarted1 by remember { mutableStateOf(false) }
//            var focusedLeftYet1 by remember { mutableStateOf(false) }
//            var selectedOption2 by remember { mutableStateOf("") }
//            val radioOptions2 = listOf("Option 1", "Option 2","Other")
//            var otherText2 by remember { mutableStateOf("") }
//            RB(
//                options = radioOptions2,
//                selectedOption = selectedOption2,
//                onOptionSelected = {selectedOption2 = it },
//                otherText = otherText2,
//                onOtherTextChange = { otherText2 = it },
//                label = "other Radio+required",
//                focusedLeftYet = focusedLeftYet1,
//                onFocusUpdate = { started ->
//                    if (started) focusStarted1 = true
//                    else if (focusStarted1) focusedLeftYet1 = true
//                }
//            )
//
//            var focusStarted2 by remember { mutableStateOf(false) }
//            var focusedLeftYet2 by remember { mutableStateOf(false) }
//            var selectedOption3 by remember { mutableStateOf("") }
//            val radioOptions3 = listOf("Option 1", "Option 2","Other")
//            RB(
//                options = radioOptions3,
//                selectedOption = selectedOption3,
//                onOptionSelected = {selectedOption3 = it },
//                label = "required",
//                focusedLeftYet = focusedLeftYet2,
//                onFocusUpdate = { started ->
//                    if (started) focusStarted2 = true
//                    else if (focusStarted2) focusedLeftYet2 = true
//                }
//            )
//
//            var selectedOption by remember { mutableStateOf("") }
//            val radioOptions = listOf("Option 1", "Option 2")
//            RB(
//                options = radioOptions,
//                selectedOption = selectedOption,
//                onOptionSelected = { selectedOption = it },
//                label = "Radiobutton"
//            )
//
//            var selectedOption1 by remember { mutableStateOf("") }
//            var otherText1 by remember { mutableStateOf("") }
//            RB(
//                options = listOf("Option 1", "Option 2", "Other"),
//                selectedOption = selectedOption1,
//                onOptionSelected = { selectedOption1 = it },
//                otherText = otherText1,
//                onOtherTextChange = { otherText1 = it },
//                label = "RadioButton with Other"
//            )

            var note by remember { mutableStateOf("") }
            TA(
                label = "Additional Comments", input = note, onChange = {note=it}
            )

            var file0 by remember { mutableStateOf<Uri?>(null) }
            Text("Full Robot Image")
            ImagePicker(
                onImageSelected = { uri -> file0 = uri }
            )

            var file1 by remember { mutableStateOf<Uri?>(null) }
            Text("Drive Train Image")
            ImagePicker(
                onImageSelected = { uri -> file1 = uri }
            )

            fun ColumnScope.onSubmit(): () -> Unit {
                return{
//                    val valid =//put all constraints here
//                        (selectedOption3 !=="") && (selectedOption2 !=="") && (number1.matches(pattern0) && number1!=="")
//                    if(valid){
//                        //! submit logic here
//                        //just local storage for now
//                    }else{
//                        //make validation messages appear
//                        focusedLeftYet0 = true
//                        focusedLeftYet1 = true
//                        focusedLeftYet2 = true
//                    }
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