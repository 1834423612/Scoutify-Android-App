package com.team695.scoutifyapp.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel

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
        private set

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
