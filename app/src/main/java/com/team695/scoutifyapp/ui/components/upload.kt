package com.team695.scoutifyapp.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun ImagePicker(onImageSelected: (Uri?) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri) // You can upload, store, or process this URI
    }

    Button(onClick = { launcher.launch("image/*") }) {
        Text("Choose Image")
    }
}
