package com.team695.scoutifyapp.ui.viewModels

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

class PenViewModel : ViewModel() {
    var lastDragPosition: Offset? = null

    var utensil by mutableStateOf("path")
        public set

    var currentStroke : Stroke? by mutableStateOf(null)
        private set
    var justUndid by mutableStateOf(false)
        private set
    var paths by mutableStateOf<List<Stroke>>(emptyList())

    var undoTree by mutableStateOf<List<Stroke>>(emptyList())
        private set

    // PATH MODE
    fun startPath(offset: Offset) {
        currentStroke = Stroke.Path(listOf(offset))
    }
    fun addPathPoint(offset: Offset) {
        val stroke = currentStroke as? Stroke.Path ?: return
        currentStroke = stroke.copy(points = stroke.points + offset)
    }
    fun endPath() {
        val stroke = currentStroke as? Stroke.Path ?: return

        paths = paths + stroke
        currentStroke = null

        if (justUndid) {
            undoTree = emptyList()
        }
    }
    // LABELED MODE
    fun addLabeledPoint(offset: Offset, label: String) {
        val stroke = Stroke.Labeled(offset to label)
        paths = paths + stroke
        if (justUndid) {
            undoTree = emptyList()
        }
    }

    fun pathsToJson() =
        Json.encodeToString(paths.map {
            when (it) {
                is Stroke.Path -> JsonStroke("path", it.points.map { p -> JsonOffset(p.x, p.y) })
                is Stroke.Labeled -> JsonStroke("labeled", listOf(JsonOffset(it.points.first.x, it.points.first.y)), it.points.second)
            }
        })

    fun jsonToPaths(json: String): List<Stroke> {
        val decoded = Json.decodeFromString<List<JsonStroke>>(json)

        return decoded.mapNotNull { stroke ->
            when (stroke.type) {

                "path" -> {
                    Stroke.Path(
                        stroke.points?.map { Offset(it.x, it.y) } ?: emptyList()
                    )
                }

                "labeled" -> {
                    val point = stroke.points?.firstOrNull() ?: return@mapNotNull null
                    val label = stroke.label ?: return@mapNotNull null

                    Stroke.Labeled(
                        Offset(point.x, point.y) to label
                    )
                }

                else -> null
            }
        }
    }
    // Undo last stroke
    fun undo() {
        if (paths.isNotEmpty()) {
            undoTree = undoTree + listOf(paths.last())
            paths = paths.dropLast(1)
            justUndid=true
        }
    }

    // Redo last undone stroke
    fun redo() {
        if (undoTree.isNotEmpty()) {
            paths = paths + listOf(undoTree.last())
            undoTree = undoTree.dropLast(1)
            justUndid=false
        }
    }
    fun reset(){
        undoTree=paths
        paths=emptyList()
        justUndid=false
    }
}
