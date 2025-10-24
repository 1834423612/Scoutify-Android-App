package com.team695.scoutifyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(onBack: () -> Unit) {
    var teamName by remember { mutableStateOf("") }
    var matchNumber by remember { mutableStateOf("") }

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
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = teamName,
                onValueChange = { teamName = it },
                label = { Text("Team Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = matchNumber,
                onValueChange = { matchNumber = it },
                label = { Text("Match Number") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            var selectedOption by remember { mutableStateOf("Option 1") }
            val options = listOf("Option 1", "Option 2")

            Text("test")
            Column {
                options.forEach { option ->
                    //Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(){
                        RadioButton(
                            selected = selectedOption == option,
                            onClick = { selectedOption = option }
                        )
                        Text(option, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }


            var isChecked by remember { mutableStateOf(false) }

            Row() {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it }
                )
                Text("test", modifier = Modifier.padding(start = 8.dp))
            }

//            var notes by remember { mutableStateOf("") }
//            OutlinedTextField(
//                value = notes,
//                onValueChange = { notes = it },
//                label = { Text("Notes") },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(150.dp), // optional: controls visible height
//                singleLine = false,
//                maxLines = 5 // optional: limits number of lines
//            )


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