package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.KeyboardType
import com.example.ui.components.RB
import com.team695.scoutifyapp.ui.components.CB
import com.team695.scoutifyapp.ui.components.OTF
import com.team695.scoutifyapp.ui.components.TA

//
@OptIn(ExperimentalMaterial3Api::class)
@Composable
//!todo: clicking outside of a text input should remove focus,
// scrollbar visible?,
// pictures
//turn form into components
//submit -> do submit backend
//image upload
//canvas functionality
//turn required into an array/object (i.e. the focousedLeftYet)


// required/validation, - for text: use Regex("^.+$"), radio has an example
fun FormScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Scouting Form") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            var teamName by remember { mutableStateOf("") }
            OTF(
                value = teamName,
                onChange = { teamName = it },
                title = "Enter the Team Name"
            )

            val checkboxOptionsLabels0 = listOf("Option 1", "Option 2", "Other")
            var checkboxOptionsBool0 by remember { mutableStateOf(listOf(false, false, false)) }
            var otherText0 by remember { mutableStateOf("") }
            CB(
                label = "Choose options",
                options = checkboxOptionsLabels0,
                checkedStates = checkboxOptionsBool0,
                onCheckedChange = { index, isChecked ->
                    checkboxOptionsBool0 = checkboxOptionsBool0.toMutableList().also { it[index] = isChecked }
                },
                otherText = otherText0,
                onOtherTextChange = { otherText0 = it }
            )

            val checkLabels = listOf("Option 1", "Option 2", "Other")
            var checkBools by remember { mutableStateOf(listOf(false, false, false)) }
            CB(
                label = "Choose options",
                options = checkLabels,
                checkedStates = checkBools,
                onCheckedChange = { index, isChecked ->
                    checkBools = checkBools.toMutableList().also { it[index] = isChecked }
                },
            )

            var number0 by remember { mutableStateOf("") }
            OTF(
                label = "number",
                title = "Number Keyboard",
                value=number0,
                onChange = { number0 = it },
                keyboardType = KeyboardType.Number
            )


            var number1 by remember { mutableStateOf("") }
            val pattern0 = Regex("^\\d+$")
            var focusedLeftYet0 by remember { mutableStateOf(false) }
            var focusStarted0 by remember { mutableStateOf(false) }
            OTF(
                title = "Enter a number",
                value = number1,
                onChange = { number1 = it },
                keyboardType = KeyboardType.Number,
                pattern = pattern0,
                focusedLeftYet = focusedLeftYet0,
                onFocusUpdate = { started ->
                    if (started) focusStarted0 = true
                    else if (focusStarted0) focusedLeftYet0 = true
                }
            )

            var focusStarted1 by remember { mutableStateOf(false) }
            var focusedLeftYet1 by remember { mutableStateOf(false) }
            var selectedOption2 by remember { mutableStateOf("") }
            val radioOptions2 = listOf("Option 1", "Option 2","Other")
            var otherText2 by remember { mutableStateOf("") }
            RB(
                options = radioOptions2,
                selectedOption = selectedOption2,
                onOptionSelected = {selectedOption2 = it },
                otherText = otherText2,
                onOtherTextChange = { otherText2 = it },
                label = "other Radio+required",
                focusedLeftYet = focusedLeftYet1,
                onFocusUpdate = { started ->
                    if (started) focusStarted1 = true
                    else if (focusStarted1) focusedLeftYet1 = true
                }
            )

            var focusStarted2 by remember { mutableStateOf(false) }
            var focusedLeftYet2 by remember { mutableStateOf(false) }
            var selectedOption3 by remember { mutableStateOf("") }
            val radioOptions3 = listOf("Option 1", "Option 2","Other")
            RB(
                options = radioOptions3,
                selectedOption = selectedOption3,
                onOptionSelected = {selectedOption3 = it },
                label = "required",
                focusedLeftYet = focusedLeftYet2,
                onFocusUpdate = { started ->
                    if (started) focusStarted2 = true
                    else if (focusStarted2) focusedLeftYet2 = true
                }
            )

            var selectedOption by remember { mutableStateOf("") }
            val radioOptions = listOf("Option 1", "Option 2")
            RB(
                options = radioOptions,
                selectedOption = selectedOption,
                onOptionSelected = { selectedOption = it },
                label = "Radiobutton"
            )

            var selectedOption1 by remember { mutableStateOf("") }
            var otherText1 by remember { mutableStateOf("") }
            RB(
                options = listOf("Option 1", "Option 2", "Other"),
                selectedOption = selectedOption1,
                onOptionSelected = { selectedOption1 = it },
                otherText = otherText1,
                onOtherTextChange = { otherText1 = it },
                label = "RadioButton with Other"
            )

            var note by remember { mutableStateOf("") }
            TA(
                label = "Additional Comments", input = note, onChange = {note=it}
            )

            fun ColumnScope.onSubmit(): () -> Unit {
                return{
                    val valid =//put all constraints here
                        (selectedOption3 !=="") && (selectedOption2 !=="") && (number1.matches(pattern0) && number1!=="")
                    if(valid){
                        //! submit logic here
                        //just local storage for now
                    }else{
                        //make validation messages appear
                        focusedLeftYet0 = true
                        focusedLeftYet1 = true
                        focusedLeftYet2 = true
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

//            Spacer(modifier = Modifier.height(20.dp))
//
//            IconButton(onClick = onBack) {
//                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
//            }
        }
    }
}


/*
most recent pit scouting form
      i: "https://lh7-us.googleusercontent.com/pUWvHrPDa5IfrQcFalk4lO0e4PhD3sLMP0jyLJU8PTWWGfw5r-Wa4qDQNHhbu0byYLzXScP5lfTSUCsvbNI-FlwDY2L7Ra0-TgYqf5Eabw0INSFE3ah4QCqCqHFrsaPKyCOt8m2Yo-H2ie9E7apzh6c8AO147A",
      w: "50%",
      question: "Type of drive train",
      description: "Select the type of drive train used in your robot design.",
      type: "radio",
      options: [
        'Tank Drive ("skid steer", plates on both sides of wheels)',
        "West Coast Drive (wheels mounted off one side of tube)",
        "Swerve Drive",
        "Other",
      ],
      optionValues: [
        'Tank Drive',
        "West Coast Drive",
        "Swerve Drive",
        "Other",
      ],
      value: null,
      required: true,
      showOtherInput: false,
      otherValue: "",
      showDescription: false,
      originalIndex: 1
    },
    {
      i: "https://lh7-us.googleusercontent.com/PCI7CaG88MiY50L7AM0CVTs9dRd3NQgqW4B2rd64vmjHaNDMEHR0EkWYqv-rzHBnGBC08NzWtr7W97lIk226Q9WVCPuTKuOSZcpb6eyNC5Q3HGmFQwp8005gRcxiS09RjeWUJQJTK-vQGDWd0QAbpSipLSkExw",
      w: "100%",
      question: "Type of wheels used",
      description: "Choose the type of wheels used on your robot.",
      type: "radio",
      options: [
        "Traction",
        "Mecanum (rollers at 45° angle)",
        "Omni (rollers at 90° angle)",
        "Other",
      ],
      optionValues: [
        "Traction",
        "Mecanum",
        "Omni",
        "Other",
      ],
      value: null,
      required: true,
      showOtherInput: false,
      otherValue: "",
      showDescription: false,
      originalIndex: 2
    },
    {
      question: "Intake Use:",
      type: "checkbox",
      options: ["Ground", "Station", "None", "Other"],
      optionValues: ["Ground", "Station", "None", "Other"],
      value: [],
      required: true,
      showOtherInput: false,
      otherValue: "",
      originalIndex: 3
    },
    {
      question: "Coral Acquisition(Scoring Method):",
      type: "radio",
      options: ["None", "Coral Station Only", "Floor Only", "Coral Station and Floor"],
      optionValues: ["None", "Coral Station Only", "Floor Only", "Coral Station and Floor"],
      value: null,
      required: true,
      showOtherInput: false,
      otherValue: "",
      originalIndex: 4
    },
    {
      question: "Scoring Locations:",
      type: "checkbox",
      options: ["L1", "L2", "L3", "L4", "Algae in Processor", "Algae in Net", "Other"],
      optionValues: ["L1", "L2", "L3", "L4", "Algae in Processor", "Algae in Net", "Other"],
      value: [],
      required: true,
      showOtherInput: false,
      otherValue: "",
      originalIndex: 5
    },
    {
      question: "Algae Acquisition(Scoring Method):",
      type: "radio",
      options: ["None", "Reef Only", "Floor Only", "Reef and Floor"],
      optionValues: ["None", "Reef Only", "Floor Only", "Reef and Floor"],
      value: null,
      required: true,
      showOtherInput: false,
      otherValue: "",
      originalIndex: 6
    },
    {
      question: "Algae Scoring:",
      type: "radio",
      options: ["None", "Processor Only", "Net Only", "Processor and Net"],
      optionValues: ["None", "Processor Only", "Net Only", "Processor and Net"],
      value: null,
      required: true,
      showOtherInput: false,
      otherValue: "",
      originalIndex: 7
    },
    {
      question: "Cage Climbing:",
      type: "checkbox",
      options: ["Deep Climb", "Shallow Climb", "No Climb"],
      optionValues: ["Deep Climb", "Shallow Climb", "No Climb"],
      value: [],
      required: true,
      originalIndex: 8
    },
    {
      question: "Robot leaves their Starting Zone during autonomous?",
      type: "radio",
      options: ["Yes", "No"],
      optionValues: ["Yes", "No"],
      value: null,
      required: true,
      showOtherInput: false,
      otherValue: "",
      originalIndex: 9
    },
    {
      question: "Robot Weight (without Bumpers)",
      description: "Enter the weight of the robot in pounds.",
      type: "number",
      required: true,
      value: null,
      originalIndex: 10
    },
    {
      question: "Bumpers Weight",
      description: "Enter the weight of the bumpers in pounds.",
      type: "number",
      required: true,
      value: null,
      originalIndex: 11
    },
    {
      question: "Robot Length (without Bumpers)",
      description: "Enter the length of the robot in inches without bumpers(front to back).",
      type: "number",
      required: true,
      value: null,
      originalIndex: 12
    },
    {
      question: "Robot Width (without Bumpers)",
      description: "Enter the width of the robot in inches without bumpers(left to right).",
      type: "number",
      required: true,
      value: null,
      originalIndex: 13
    },
    {
      question: "Robot Height",
      description: "Enter the height of the robot in inches from the floor to the highest point on the robot at the start of the match.",
      type: "number",
      required: true,
      value: null,
      originalIndex: 14
    },
    {
      question: "Height when fully extended",
      description: "In inches.",
      type: "number",
      required: true,
      value: null,
      originalIndex: 15
    },
    {
      question: "Drive Team Members",
      type: "radio",
      options: [
        "One person driving and operating the robot during a match",
        "Other",
      ],
      optionValues: [
        "One person driving and operating the robot during a match",
        "Other",
      ],
      value: null,
      required: true,
      showOtherInput: false,
      otherValue: "",
      originalIndex: 16
    },
    {
      question: "Hours/Weeks of Practice",
      type: "text",
      required: true,
      value: null,
      originalIndex: 17
    },
    {
      question: "Additional Comments",
      type: "textarea",
      required: false,
      value: null,
      originalIndex: 18


 */