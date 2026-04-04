package com.team695.scoutifyapp.ui.viewModels

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team695.scoutifyapp.data.api.model.GameDetails
import com.team695.scoutifyapp.data.api.model.JsonOffset
import com.team695.scoutifyapp.data.api.model.JsonStroke
import com.team695.scoutifyapp.data.api.model.Stroke
import com.team695.scoutifyapp.data.api.model.Task
import com.team695.scoutifyapp.data.repository.GameDetailRepository
import com.team695.scoutifyapp.data.repository.MatchRepository
import com.team695.scoutifyapp.data.repository.TaskRepository
import com.team695.scoutifyapp.data.types.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private data class RestoredTeleopState(
    val section: TeleopSection,
    val totalMilliseconds: Int,
    val cachedMilliseconds: Int = 0,
)

@OptIn(FlowPreview::class)
class DataViewModel(
    private val gameDetailRepository: GameDetailRepository,
    private val taskRepository: TaskRepository,
    private val matchRepository: MatchRepository,
    private val taskId: Int,
) : ViewModel() {
    private val persistMutex = Mutex()
    private var lastLiveTeleopPersistTotalMilliseconds = -1
    private var lastLiveTeleopPersistCachedMilliseconds = -1

    private val _formState = MutableStateFlow(
        GameFormState(
            teamNumber = -1,
            gameDetails = GameDetails(),
            teleopSection = TeleopSection.UNSTARTED,
        )
    )
    val formState: StateFlow<GameFormState> = _formState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val gameDetails: GameDetails = gameDetailRepository.getGameDetailsByTaskId(taskId)

            val taskResult: Result<Task> = taskRepository.getTaskById(taskId)
            val task: Task? = taskResult.getOrNull()
            val matchNum: Int = task?.matchNum ?: -2

            if (matchNum == -2) {
                throw Error("Could not find task_id $taskId for game details")
            }

            Log.d("GAME_DETAILS_SAVED", gameDetails.toString())
            val restoredTeleopState = restoreTeleopState(gameDetails)

            _formState.update {
                it.copy(
                    teamNumber = task?.teamNum ?: -2,
                    gameDetails = gameDetails,
                    teleopSection = restoredTeleopState.section,
                    teleopTotalMilliseconds = restoredTeleopState.totalMilliseconds,
                    teleopCachedMilliseconds = restoredTeleopState.cachedMilliseconds,
                    paths = gameDetails.autonPath
                        ?.let { jsonToPaths(gameDetails.autonPath) }
                        ?: emptyList()
                )
            }
        }

        _formState
            .debounce(2000L)
            .distinctUntilChanged() // Only save if the state actually changed
            .onEach { currentFormState: GameFormState ->
                //println("FORM STATE: $currentFormState")
                // ignore if critical fields are not set - still waiting to be loaded from the database
                if (
                    currentFormState.gameDetails.task_id == null ||
                    currentFormState.gameDetails.matchNumber == null ||
                    currentFormState.gameDetails.alliance == null ||
                    currentFormState.gameDetails.alliancePosition == null
                ) {
                    Log.d("Dataviewmodel", "Do not save ${currentFormState.gameDetails.robotOnField}")
                    return@onEach
                }
                Log.d("Dataviewmodel", "Save ${currentFormState.gameDetails.robotOnField}")
                persistFormState(currentFormState)
            }
            .launchIn(viewModelScope)
    }

    private suspend fun persistFormState(currentFormState: GameFormState) {
        persistMutex.withLock {
            val gameDetails: GameDetails = currentFormState.gameDetails.copy(
                autonPath = pathsToJson(currentFormState.paths),
                localTeleopSection = currentFormState.teleopSection.name,
                localTeleopTotalMilliseconds = currentFormState.teleopTotalMilliseconds,
                localTeleopCachedMilliseconds = currentFormState.teleopCachedMilliseconds,
            )

            Log.d("DATA_MODEL", gameDetails.toString())

            // save game details
            gameDetailRepository.updateDbFromGameDetails(gameDetails)

            // update task progress
            taskRepository.updateTaskProgress(
                id = currentFormState.gameDetails.task_id!!,
                progress = currentFormState.totalProgress
            )
        }
    }

    suspend fun flushNow() {
        val currentFormState = _formState.value
        if (
            currentFormState.gameDetails.task_id == null ||
            currentFormState.gameDetails.matchNumber == null ||
            currentFormState.gameDetails.alliance == null ||
            currentFormState.gameDetails.alliancePosition == null
        ) {
            return
        }

        persistFormState(currentFormState)
    }

    fun flushNowAsync() {
        viewModelScope.launch(Dispatchers.IO) {
            flushNow()
        }
    }

    private fun flushAfterStateMutation() {
        flushNowAsync()
    }

    private fun maybeFlushLiveTeleopSnapshot() {
        val currentState = _formState.value
        if (!currentState.teleopRunning && currentState.teleopCachedMilliseconds <= 0) {
            return
        }

        val totalMilliseconds = currentState.teleopTotalMilliseconds
        val cachedMilliseconds = currentState.teleopCachedMilliseconds
        val totalDelta = kotlin.math.abs(totalMilliseconds - lastLiveTeleopPersistTotalMilliseconds)
        val cachedDelta = kotlin.math.abs(cachedMilliseconds - lastLiveTeleopPersistCachedMilliseconds)

        if (totalDelta < 1000 && cachedDelta < 1000) {
            return
        }

        lastLiveTeleopPersistTotalMilliseconds = totalMilliseconds
        lastLiveTeleopPersistCachedMilliseconds = cachedMilliseconds
        flushNowAsync()
    }

    fun formEvent(gameDetails: GameDetails) {
        if (gameDetails.task_id == null) {
            Log.d("DataViewModel", "Invalid operation", Throwable())
            return
        }
        _formState.update {
            it.copy(
                gameDetails = gameDetails
            )
        }
        flushAfterStateMutation()
    }

    fun getAllianceForMatch(matchNum: Long, teamNum: Long): Char {
        return matchRepository.getAllianceForMatch(
            matchNumber = matchNum,
            teamNumber = teamNum
        )
    }

    fun toggleWarningModal(title: String, text: String) {
        _formState.update {
            it.copy(
                showWarningModal = !it.showWarningModal,
                warningModalTitle = title,
                warningModalText = text,
            )
        }
    }

    //deltaTime is in milliseconds
    fun updateTime(deltaTime: Int) {
        if (!_formState.value.teleopRunning) {
            val dtEndgame = ENDGAME_END_TIME - _formState.value.teleopTotalMilliseconds
            //println("ENDGAME DELTA: $dtEndgame")
            _formState.update {
                it.copy(
                    teleopTotalMilliseconds = ENDGAME_END_TIME,
                    teleopCachedMilliseconds = it.teleopCachedMilliseconds + dtEndgame
                )
            }
        } else {
            _formState.update {
                it.copy(
                    teleopTotalMilliseconds = it.teleopTotalMilliseconds + deltaTime,
                    teleopCachedMilliseconds = it.teleopCachedMilliseconds + deltaTime,
                )
            }
        }
        maybeFlushLiveTeleopSnapshot()
    }

    fun resetCacheTime() {
        _formState.update {
            it.copy(
                teleopCachedMilliseconds = 0
            )
        }
        flushAfterStateMutation()
    }

    //resets all teleop data and starts teleop
    fun startTeleop() {
        _formState.update {
            it.copy(
                teleopRunning = true,
                teleopSection = TeleopSection.TRANSITION,
                teleopTotalMilliseconds = 0,
                teleopCachedMilliseconds = 0,
                gameDetails = it.gameDetails.copy(
                    //transition
                    transitionCyclingTime = null,
                    transitionStockpilingTime = null,
                    transitionDefendingTime = null,
                    transitionBrokenTime = null,
                    transitionFirstActive = null,

                    // 1st Shift
                    shift1CyclingTime = null,
                    shift1StockpilingTime = null,
                    shift1DefendingTime = null,
                    shift1BrokenTime = null,

                    // 2nd Shift
                    shift2CyclingTime = null,
                    shift2StockpilingTime = null,
                    shift2DefendingTime = null,
                    shift2BrokenTime = null,

                    // 3rd Shift
                    shift3CyclingTime = null,
                    shift3StockpilingTime = null,
                    shift3DefendingTime = null,
                    shift3BrokenTime = null,

                    // 4th Shift
                    shift4CyclingTime = null,
                    shift4StockpilingTime = null,
                    shift4DefendingTime = null,
                    shift4BrokenTime = null,

                    // Endgame
                    endgameCyclingTime = null,
                    endgameStockpilingTime = null,
                    endgameDefendingTime = null,
                    endgameBrokenTime = null,

                    endgameAttemptsClimb = null,
                    endgameClimbSuccess = null,
                    endgameClimbPosition = null,
                    endgameClimbCode = null,
                    teleopCompleted = false,
                    teleopFuelCount = null,
                    postgameShootAnywhere = null,
                    postgameShootWhileMoving = null,
                    postgameStockpileNeutral = null,
                    postgameStockpileCrossCourt = null,
                    postgameFeedOutpost = null,
                    postgameReceiveOutpost = null,
                    postgameUnderTrench = null,
                    postgameOverBump = null,
                    postgameFlag = null,
                )
            )
        }
        flushAfterStateMutation()
    }

    fun endTeleop() {
        _formState.update {
            it.copy(
                teleopRunning = false,
            )
        }
        flushAfterStateMutation()
    }

    fun completeTeleop() {
        _formState.update {
            it.copy(
                teleopRunning = false,
                teleopSection = TeleopSection.ENDED,
                teleopTotalMilliseconds = ENDGAME_END_TIME,
                teleopCachedMilliseconds = 0,
                gameDetails = it.gameDetails.copy(
                    teleopCompleted = true
                )
            )
        }
        flushAfterStateMutation()
    }

    fun setTeleopSection(teleopSection: TeleopSection, teleopTotalMilliseconds: Int) {
        _formState.update {
            it.copy(
                teleopSection = teleopSection,
                teleopTotalMilliseconds = teleopTotalMilliseconds
            )
        }
        flushAfterStateMutation()
    }

    fun startPath(offset: Offset) {
        _formState.update {
            it.copy(
                currentStroke = Stroke.Path(listOf(offset))
            )
        }
    }

    fun addPathPoint(offset: Offset) {
        val stroke = _formState.value.currentStroke as? Stroke.Path ?: return
        _formState.update {
            it.copy(
                currentStroke = stroke.copy(points = stroke.points + offset)
            )
        }
    }

    fun endPath() {
        val stroke = _formState.value.currentStroke as? Stroke.Path ?: return

        _formState.update {
            it.copy(
                paths = _formState.value.paths + stroke,
                currentStroke = null,
                undoTree = if (_formState.value.justUndid) emptyList()
                else _formState.value.undoTree
            )
        }
        flushAfterStateMutation()
    }

    fun addLabeledPoint(offset: Offset, label: String) {
        val stroke = Stroke.Labeled(offset to label)

        _formState.update {
            it.copy(
                paths = _formState.value.paths + stroke,
                undoTree = if (_formState.value.justUndid) emptyList()
                else _formState.value.undoTree
            )
        }
        flushAfterStateMutation()
    }

    private fun pathsToJson(paths: List<Stroke>) =
        Json.encodeToString(paths.map {
            when (it) {
                is Stroke.Path ->
                    JsonStroke(
                        type = "path",
                        points = it.points.map { p -> JsonOffset(p.x, p.y) })
                is Stroke.Labeled ->
                    JsonStroke(
                        type = "labeled",
                        points = listOf(
                            JsonOffset(it.points.first.x, it.points.first.y)),
                        label = it.points.second)
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

    fun undo() {
        if (_formState.value.paths.isNotEmpty()) {
            _formState.update {
                it.copy(
                    paths = _formState.value.paths.dropLast(1),
                    undoTree = _formState.value.undoTree + listOf(_formState.value.paths.last()),
                    justUndid = true
                )
            }
            flushAfterStateMutation()
        }
    }

    fun redo() {
        if (_formState.value.undoTree.isNotEmpty()) {
            _formState.update {
                it.copy(
                    paths = _formState.value.paths + listOf(_formState.value.undoTree.last()),
                    undoTree = _formState.value.undoTree.dropLast(1),
                    justUndid = false
                )
            }
            flushAfterStateMutation()
        }
    }

    fun setUtensil(utensil: String) {
        _formState.update {
            it.copy(
                utensil = utensil
            )
        }
    }

    fun reset() {
        _formState.update {
            it.copy(
                undoTree = _formState.value.paths,
                paths = emptyList(),
                justUndid = false
            )
        }
        flushAfterStateMutation()
    }

    private fun restoreTeleopState(gameDetails: GameDetails): RestoredTeleopState {
        val hasPersistedLocalTeleopState =
            gameDetails.localTeleopSection != null &&
            gameDetails.localTeleopTotalMilliseconds != null &&
            gameDetails.localTeleopCachedMilliseconds != null &&
            (
                gameDetails.localTeleopSection != TeleopSection.UNSTARTED.name ||
                    gameDetails.localTeleopTotalMilliseconds != 0 ||
                    gameDetails.localTeleopCachedMilliseconds != 0
                )

        if (hasPersistedLocalTeleopState) {
            val persistedSection = TeleopSection.entries.find {
                it.name == gameDetails.localTeleopSection
            } ?: TeleopSection.UNSTARTED

            return RestoredTeleopState(
                section = persistedSection,
                totalMilliseconds = gameDetails.localTeleopTotalMilliseconds,
                cachedMilliseconds = gameDetails.localTeleopCachedMilliseconds
            )
        }

        if (gameDetails.teleopCompleted == true || gameDetails.postgameProgress > 0f) {
            return RestoredTeleopState(
                section = TeleopSection.ENDED,
                totalMilliseconds = ENDGAME_END_TIME
            )
        }

        val transitionTotal = listOf(
            gameDetails.transitionCyclingTime,
            gameDetails.transitionStockpilingTime,
            gameDetails.transitionDefendingTime,
            gameDetails.transitionBrokenTime,
        ).sumOf { it ?: 0 }
        val shift1Total = listOf(
            gameDetails.shift1CyclingTime,
            gameDetails.shift1StockpilingTime,
            gameDetails.shift1DefendingTime,
            gameDetails.shift1BrokenTime,
        ).sumOf { it ?: 0 }
        val shift2Total = listOf(
            gameDetails.shift2CyclingTime,
            gameDetails.shift2StockpilingTime,
            gameDetails.shift2DefendingTime,
            gameDetails.shift2BrokenTime,
        ).sumOf { it ?: 0 }
        val shift3Total = listOf(
            gameDetails.shift3CyclingTime,
            gameDetails.shift3StockpilingTime,
            gameDetails.shift3DefendingTime,
            gameDetails.shift3BrokenTime,
        ).sumOf { it ?: 0 }
        val shift4Total = listOf(
            gameDetails.shift4CyclingTime,
            gameDetails.shift4StockpilingTime,
            gameDetails.shift4DefendingTime,
            gameDetails.shift4BrokenTime,
        ).sumOf { it ?: 0 }
        val endgameTotal = listOf(
            gameDetails.endgameCyclingTime,
            gameDetails.endgameStockpilingTime,
            gameDetails.endgameDefendingTime,
            gameDetails.endgameBrokenTime,
        ).sumOf { it ?: 0 }

        fun hasValue(value: Any?): Boolean = when (value) {
            null -> false
            is String -> value.isNotBlank()
            else -> true
        }

        fun hasAny(vararg values: Any?): Boolean = values.any(::hasValue)

        val hasTransitionData = transitionTotal > 0 || hasValue(gameDetails.transitionFirstActive)
        val hasShift1Data = shift1Total > 0
        val hasShift2Data = shift2Total > 0
        val hasShift3Data = shift3Total > 0
        val hasShift4Data = shift4Total > 0
        val hasEndgameData = endgameTotal > 0 || hasAny(
            gameDetails.endgameAttemptsClimb,
            gameDetails.endgameClimbSuccess,
            gameDetails.endgameClimbPosition,
            gameDetails.endgameClimbCode
        )

        return when {
            hasEndgameData -> RestoredTeleopState(
                section = TeleopSection.ENDGAME,
                totalMilliseconds = (SHIFT4_END_TIME + endgameTotal).coerceIn(
                    SHIFT4_END_TIME,
                    ENDGAME_END_TIME
                )
            )
            hasShift4Data -> RestoredTeleopState(
                section = TeleopSection.SHIFT4,
                totalMilliseconds = (SHIFT3_END_TIME + shift4Total).coerceIn(
                    SHIFT3_END_TIME,
                    SHIFT4_END_TIME
                )
            )
            hasShift3Data -> RestoredTeleopState(
                section = TeleopSection.SHIFT3,
                totalMilliseconds = (SHIFT2_END_TIME + shift3Total).coerceIn(
                    SHIFT2_END_TIME,
                    SHIFT3_END_TIME
                )
            )
            hasShift2Data -> RestoredTeleopState(
                section = TeleopSection.SHIFT2,
                totalMilliseconds = (SHIFT1_END_TIME + shift2Total).coerceIn(
                    SHIFT1_END_TIME,
                    SHIFT2_END_TIME
                )
            )
            hasShift1Data -> RestoredTeleopState(
                section = TeleopSection.SHIFT1,
                totalMilliseconds = (TRANSITION_END_TIME + shift1Total).coerceIn(
                    TRANSITION_END_TIME,
                    SHIFT1_END_TIME
                )
            )
            hasTransitionData -> RestoredTeleopState(
                section = TeleopSection.TRANSITION,
                totalMilliseconds = transitionTotal.coerceIn(0, TRANSITION_END_TIME)
            )
            else -> RestoredTeleopState(
                section = TeleopSection.UNSTARTED,
                totalMilliseconds = 0
            )
        }
    }
}
