package com.team695.scoutifyapp.ui.screens

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.team695.scoutifyapp.data.api.model.CommentBody
import com.team695.scoutifyapp.data.api.model.Match
import com.team695.scoutifyapp.data.repository.CommentRepository
import com.team695.scoutifyapp.data.repository.MatchRepository
import com.team695.scoutifyapp.data.types.SaveStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CommentsViewModel (
    private val commentRepository: CommentRepository,
    private val matchRepository: MatchRepository
) : ViewModel() {
    // get match stuff
    val matches: StateFlow<List<Match>> =
        matchRepository.matches
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )


    // State variables for comments

    private val _saveStatus = mutableStateOf(SaveStatus.IDLE)
    val saveStatus: State<SaveStatus> = _saveStatus
    private val _selectedMatch = mutableStateOf("1") // DEFAULT MATCH NUMBER THAT SHOWS UP WHEN YOU GO INTO COMMENTS PAGE
    val selectedMatch: State<String> = _selectedMatch

    private val _red1Comment = mutableStateOf("")
    val red1Comment: State<String> = _red1Comment

    private val _red2Comment = mutableStateOf("")
    val red2Comment: State<String> = _red2Comment

    private val _red3Comment = mutableStateOf("")
    val red3Comment: State<String> = _red3Comment

    private val _blue1Comment = mutableStateOf("")
    val blue1Comment: State<String> = _blue1Comment

    private val _blue2Comment = mutableStateOf("")
    val blue2Comment: State<String> = _blue2Comment

    private val _blue3Comment = mutableStateOf("")
    val blue3Comment: State<String> = _blue3Comment

    // Auto-save state
    private val _autoSaved = mutableStateOf(false)
    val autoSaved: State<Boolean> = _autoSaved

    // Submission status
    private val _isSubmitted = mutableStateOf(false)
    val isSubmitted: State<Boolean> = _isSubmitted
    // Auto-save timeout job
    private var autoSaveJob: Job? = null

    // Function to handle match selection
    fun onMatchSelected(match: String) {
        autoSaveJob?.cancel()
        _selectedMatch.value = match
        match.toIntOrNull()?.let { // safe check cuz it was crashing when going to home
            fetchComments(it)
        }
    }

    // Function to handle comment change
    fun onCommentChanged(alliance: String, position: Int, comment: String) {
        // check if the thang is being changed ts
        if (_isSubmitted.value) {
            _isSubmitted.value = false

            val matchNum = _selectedMatch.value.toIntOrNull()
            if (matchNum != null) {
                viewModelScope.launch {
                    commentRepository.updateSubmissionStatus(matchNum, 0)
                }
            }
        }

        when (alliance) {
            "Red" -> {
                when (position) {
                    1 -> _red1Comment.value = comment
                    2 -> _red2Comment.value = comment
                    3 -> _red3Comment.value = comment
                }
            }
            "Blue" -> {
                when (position) {
                    1 -> _blue1Comment.value = comment
                    2 -> _blue2Comment.value = comment
                    3 -> _blue3Comment.value = comment
                }
            }
        }

        resetAutoSave()  // Reset the auto-save timer whenever a comment changes
    }

    fun setCommentsAsSubmitted () {
        val matchNum = _selectedMatch.value.toIntOrNull() ?: return
        viewModelScope.launch {
            try {
                commentRepository.updateSubmissionStatus(matchNum, 1)
                _isSubmitted.value = true
                _saveStatus.value = SaveStatus.SUBMITTED
            }
            catch(e: Exception) {
                _saveStatus.value = SaveStatus.ERROR
            }
        }
    }

    // Function to save comments and update 'submitted' to 1
    fun submitComments() {
        val matchNum = _selectedMatch.value.toIntOrNull() ?: return

        viewModelScope.launch {
            try {
                val commentsToSave = mutableListOf<CommentBody>()

                fun addIfNotEmpty(
                    team: Int,
                    alliance: String,
                    position: Int,
                    comment: String
                ) {
                    if (comment.isNotBlank()) {
                        commentsToSave.add(
                            CommentBody(
                                match_number = matchNum,
                                team_number = team,
                                alliance = alliance,
                                alliance_position = position,
                                comment = comment,
                                timestamp = System.currentTimeMillis(),
                                submitted = if (_isSubmitted.value) 1 else 0
                            )
                        )
                    }
                }

                val match = matches.value.getOrNull(matchNum.toInt() - 1)
                match?.let {
                    addIfNotEmpty(it.redAlliance[0], "R", 1, _red1Comment.value)
                    addIfNotEmpty(it.redAlliance[1], "R", 2, _red2Comment.value)
                    addIfNotEmpty(it.redAlliance[2], "R", 3, _red3Comment.value)
                    addIfNotEmpty(it.blueAlliance[0], "B", 1, _blue1Comment.value)
                    addIfNotEmpty(it.blueAlliance[1], "B", 2, _blue2Comment.value)
                    addIfNotEmpty(it.blueAlliance[2], "B", 3, _blue3Comment.value)
                }

                if (commentsToSave.isNotEmpty()) {
                    commentRepository.saveComments(commentsToSave)
                }

                showAutosaved()
            }
            catch(e: Exception) {
                _saveStatus.value = SaveStatus.ERROR
            }
        }
    }

    fun printDB () {
        viewModelScope.launch {
            commentRepository.printAllComments()
        }
    }


    // Reset auto-save timer when there's a new activity (comment changed, etc.)
    private fun resetAutoSave() {
        _autoSaved.value = false

        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(1000)
            submitComments()
            _autoSaved.value = true
        }
    }

    private fun showAutosaved() {
        viewModelScope.launch {
            _saveStatus.value = SaveStatus.AUTOSAVED
            delay(4000)
            _saveStatus.value = SaveStatus.IDLE
        }
    }


    private fun fetchComments(match: Int) {
        viewModelScope.launch {
            val comments = commentRepository.getCommentsByMatchNumber(match)

            _red1Comment.value = comments.firstOrNull { it.alliance == "R" && it.alliance_position == 1 }?.comment ?: ""
            _red2Comment.value = comments.firstOrNull { it.alliance == "R" && it.alliance_position == 2 }?.comment ?: ""
            _red3Comment.value = comments.firstOrNull { it.alliance == "R" && it.alliance_position == 3 }?.comment ?: ""
            _blue1Comment.value = comments.firstOrNull { it.alliance == "B" && it.alliance_position == 1 }?.comment ?: ""
            _blue2Comment.value = comments.firstOrNull { it.alliance == "B" && it.alliance_position == 2 }?.comment ?: ""
            _blue3Comment.value = comments.firstOrNull { it.alliance == "B" && it.alliance_position == 3 }?.comment ?: ""

            _isSubmitted.value = comments.any { it.submitted == 1 }
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoSaveJob?.cancel() // Clean up the auto-save job when the ViewModel is destroyed
    }
}
