package com.team695.scoutifyapp.data.api.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.serialization.Serializable
import kotlin.collections.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString


@kotlinx.serialization.Serializable
data class JsonStroke(
    val type: String,
    val points: List<JsonOffset>? = null,
    val label: String? = null
)

@Serializable
data class JsonOffset(
    val x: Float,
    val y: Float
)


sealed class Stroke {
    data class Path(val points: List<Offset>) : Stroke()
    data class Labeled(val points: Pair<Offset, String>) : Stroke()
}