package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
//
@OptIn(ExperimentalMaterial3Api::class)
@Composable
//!todo: clicking outside of a text input should remove focus,
// scrollbar visible?,
// picture,
// required/validation, - for text: use Regex("^.+$"), what about radio and checkbox?
// autofill
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
                //.weight(1f)
                .verticalScroll(scrollState)
                //.padding(end = 12.dp)//for scroll bar
        ) {
            var teamName by remember { mutableStateOf("") }
            OutlinedTextField(
                value = teamName,
                onValueChange = { teamName = it },
                label = { Text("Team Name") },
                modifier = Modifier.fillMaxWidth()
            )//team name
            Spacer(modifier = Modifier.height(12.dp))

            var matchNumber by remember { mutableStateOf("") }
            OutlinedTextField(
                value = matchNumber,
                onValueChange = { matchNumber = it },
                label = { Text("Match Number") },
                modifier = Modifier.fillMaxWidth()
            )// match number
            Spacer(modifier = Modifier.height(12.dp))

            var selectedOption0 by remember { mutableStateOf("") }
            val radioOptions0 = listOf("Option 1", "Option 2")
            Text("radiobutton")
            Column {
                radioOptions0.forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedOption0 == option,
                            onClick = { selectedOption0 = option }
                        )
                        Text(option, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            var selectedOption1 by remember { mutableStateOf("") }
            val radioOptions1 = listOf("Option 1", "Option 2","Other")
            var otherText1 by remember { mutableStateOf("") }
            Text("radiobutton with other")
            Column {
                radioOptions1.forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedOption1 == option,
                            onClick = { selectedOption1 = option }
                        )
                        Text(option, modifier = Modifier.padding(start = 8.dp))
                        if(option=="Other"){
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                        value = otherText1,
                                onValueChange = { otherText1 = it },
                                label = { Text("Enter Response") },
                                        modifier = Modifier.fillMaxWidth().padding(start = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text("checkbox with other")
            val checkboxOptionsLabels0 = listOf("Option 1", "Option 2","Other")
            var checkboxOptionsBool0 by remember { mutableStateOf(listOf(false,false,false)) }
            var otherText0 by remember { mutableStateOf("") }
            Column {
                checkboxOptionsLabels0.forEachIndexed { index, option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = checkboxOptionsBool0[index],
                            onCheckedChange = {
                                checkboxOptionsBool0 = checkboxOptionsBool0.toMutableList()
                                    .also { it[index] = !checkboxOptionsBool0[index] }
                            }
                        )
                        Text(option, modifier = Modifier.padding(start = 8.dp))
                        if(option=="Other"){
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = otherText0,
                                onValueChange = { otherText0 = it },
                                label = { Text("Enter Response") },
                                modifier = Modifier.fillMaxWidth().padding(start = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text("checkbox")
            val checkboxOptionsLabels1 = listOf("Option 1", "Option 2")
            var checkboxOptionsBool1 by remember { mutableStateOf(listOf(false,false)) }
            Column {
                checkboxOptionsLabels1.forEachIndexed { index, option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = checkboxOptionsBool1[index],
                            onCheckedChange = {
                                checkboxOptionsBool1 = checkboxOptionsBool1.toMutableList()
                                    .also { it[index] = !checkboxOptionsBool1[index] }
                            }
                        )
                        Text(option, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            Text("Number keyboard")
            var number0 by remember { mutableStateOf("") }
            OutlinedTextField(
                value=number0,
                onValueChange = { number0 = it },
                label = { Text("Label") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Validation: integer example")
            val pattern0 = Regex("^\\d+$")
            var number1 by remember { mutableStateOf("") }
            var focusStarted0 by remember { mutableStateOf(false) }
            var focusedLeftYet0 by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = number1,
                onValueChange = { number1 = it },
                label = {
                    if (!focusedLeftYet0 || number1.matches(pattern0)) {
                        print(focusedLeftYet0)
                        Text("Enter Response")
                    } else {
                        Text("Invalid input", color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if(focusState.isFocused) focusStarted0=true//this clause fixes the initial 'onload set focus state' error
                        else if (!focusState.isFocused&&focusStarted0) {
                            focusedLeftYet0 = true
                        }
                    },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )


            Text("Additional Comments")
            var notes by remember { mutableStateOf("") }
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Enter Response") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp), // optional: controls visible height
                singleLine = false,
                maxLines = 5 // optional: limits number of lines
            )

            Spacer(modifier = Modifier.height(20.dp))

            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
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