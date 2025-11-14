package com.team695.scoutifyapp.ui.components


import com.team695.scoutifyapp.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun Bison() {
    Image(
        painter = painterResource(id = R.drawable.bison),
        contentDescription = "Bison",
        modifier = Modifier.size(120.dp)
    )
}
