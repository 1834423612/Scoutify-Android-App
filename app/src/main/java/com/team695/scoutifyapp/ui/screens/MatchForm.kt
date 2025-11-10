package com.team695.scoutifyapp.ui.screens

import android.net.Uri
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.components.RB
import com.team695.scoutifyapp.ui.components.CB
import com.team695.scoutifyapp.ui.components.ImagePicker
import com.team695.scoutifyapp.ui.components.InfoCard
import com.team695.scoutifyapp.ui.components.OTF
import com.team695.scoutifyapp.ui.components.Required
import com.team695.scoutifyapp.ui.components.TA

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



            var note by remember { mutableStateOf("") }
            TA(
                label = "Additional Comments", input = note, onChange = {note=it}
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
        }
    }
}
