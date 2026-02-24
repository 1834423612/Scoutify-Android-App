package com.team695.scoutifyapp.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel

class PenViewModel : ViewModel() {

    var utensil by mutableStateOf("path")
        public set

    var currentPath by mutableStateOf<List<Offset>>(emptyList())
        private set
    var justUndid by mutableStateOf(false)
        private set
    var paths by mutableStateOf<List<List<Offset>>>(emptyList())
        private set

    var undoTree by mutableStateOf<List<List<Offset>>>(emptyList())
        private set

    // Start a new stroke
    fun startStroke(offset: Offset) {
        currentPath = listOf(offset)
    }

    // Add a point to the current stroke
    fun addPoint(offset: Offset) {
        currentPath = currentPath + offset
    }

    // Finish the stroke and commit it
    fun endStroke() {
        if (currentPath.isNotEmpty()) {
            paths = paths + listOf(currentPath)
            currentPath = emptyList()
            if(justUndid){ undoTree=emptyList() }

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
