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
import com.team695.scoutifyapp.data.api.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.team695.scoutifyapp.data.repository.GameDetailRepository
import com.team695.scoutifyapp.data.repository.MatchRepository
import com.team695.scoutifyapp.data.repository.TaskRepository
import com.team695.scoutifyapp.data.types.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.collections.plus
import kotlin.plus


@OptIn(FlowPreview::class)
class DataViewModel(
    private val gameDetailRepository: GameDetailRepository,
    private val taskRepository: TaskRepository,
    private val matchRepository: MatchRepository,
    private val taskId: Int,
) : ViewModel() {
    private val _formState = MutableStateFlow(
        GameFormState(
            matchNum = -1,
            teamNumber = -1,
            alliance = "R", //default to Red alliance
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
            val teamNum: Int = task?.teamNum ?: -2
            var alliance = "R"

            if(matchNum != -2) {
                alliance = getAllianceForMatch(matchNum.toLong(), teamNum.toLong())
            }
            else {
                Log.e("DataViewModel", "Failed to find task for match")
            }


            _formState.update {
                it.copy(
                    teamNumber = task?.teamNum ?: -2,
                    matchNum = task?.matchNum ?: -2,
                    alliance = alliance,
                    gameDetails = gameDetails,
                    teleopSection = if(gameDetails.teleopCompleted == true) TeleopSection.ENDED else TeleopSection.UNSTARTED,
                    paths = gameDetails.autonPath
                        ?.let { jsonToPaths( gameDetails.autonPath) }
                        ?: emptyList()
                )
            }
        }

        _formState
            .debounce(2000L)
            .distinctUntilChanged() // Only save if the state actually changed
            .onEach { currentFormState: GameFormState ->

                //ignore if taskId is not set - still waiting to be loaded from the database
                if(currentFormState.gameDetails.task_id == null) {
                    return@onEach
                }

                //update auton path in game details, bypass the state flow

                val gameDetails: GameDetails = currentFormState.gameDetails.copy(
                    autonPath = pathsToJson()
                )

                Log.d("DATA_MODEL", gameDetails.toString())


                //save game details
                gameDetailRepository.updateDbFromGameDetails(gameDetails)

                //update task progress
                taskRepository.updateTaskProgress(id=currentFormState.gameDetails.task_id, progress = currentFormState.totalProgress)
            }
            .launchIn(viewModelScope) // Run this in the background tied to the ViewModel's lifecycle
    }

    fun formEvent(gameDetails: GameDetails) {
        if(gameDetails.task_id == null) {
            Log.d("DataViewModel", "Invalid operation", Throwable())
            return
        }
        _formState.update {
            it.copy(
                gameDetails = gameDetails
            )
        }
    }
    fun getAllianceForMatch(matchNum:Long,teamNum: Long):String{
        Log.d("MATCH_NUM_2", matchNum.toString())
        return matchRepository.getAllianceForMatch(matchNum, teamNum)
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
        _formState.update {
            it.copy(
                teleopTotalMilliseconds = it.teleopTotalMilliseconds + deltaTime,
                teleopCachedMilliseconds = it.teleopCachedMilliseconds + deltaTime,
            )
        }
    }

    fun resetCacheTime() {
        _formState.update {
            it.copy(
                teleopCachedMilliseconds = 0
            )
        }
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
                )
            )
        }
    }

    fun endTeleop() {
        _formState.update {
            it.copy(
                teleopRunning = false,
            )
        }
    }

    fun setTeleopSection(teleopSection: TeleopSection, teleopTotalMilliseconds: Int) {
        _formState.update {
            it.copy(
                teleopSection = teleopSection,
                teleopTotalMilliseconds = teleopTotalMilliseconds
            )
        }
    }

    //--------------------------AUTON---------------------------------
    // PATH MODE
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
                undoTree = if(_formState.value.justUndid) emptyList()
                        else _formState.value.undoTree
            )
        }
    }

    // LABELED MODE
    fun addLabeledPoint(offset: Offset, label: String) {
        val stroke = Stroke.Labeled(offset to label)

        _formState.update {
            it.copy(
                paths = _formState.value.paths + stroke,
                undoTree = if(_formState.value.justUndid) emptyList()
                else _formState.value.undoTree
            )
        }
    }

    fun pathsToJson() =
        Json.encodeToString(_formState.value.paths.map {
            when (it) {
                is Stroke.Path ->
                    JsonStroke(
                        type="path",
                        points=it.points.map { p -> JsonOffset(p.x, p.y) })
                is Stroke.Labeled ->
                    JsonStroke(
                        type="labeled",
                        points=listOf(
                            JsonOffset(it.points.first.x, it.points.first.y)),
                        label=it.points.second)
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
        if (_formState.value.paths.isNotEmpty()) {
            _formState.update {
                it.copy(
                    paths = _formState.value.paths.dropLast(1),
                    undoTree = _formState.value.undoTree + listOf(_formState.value.paths.last()),
                    justUndid=true
                )
            }
        }
    }

    // Redo last undone stroke
    fun redo() {
        if (_formState.value.undoTree.isNotEmpty()) {
            _formState.update {
                it.copy(
                    paths = _formState.value.paths + listOf(_formState.value.undoTree.last()),
                    undoTree = _formState.value.undoTree.dropLast(1),
                    justUndid=false
                )
            }
        }
    }

    fun reset(){
        _formState.update {
            it.copy(
                undoTree = _formState.value.paths,
                paths = emptyList(),
                justUndid=false
            )
        }
    }
}