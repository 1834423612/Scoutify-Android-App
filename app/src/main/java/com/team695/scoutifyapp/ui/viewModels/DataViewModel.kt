package com.team695.scoutifyapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team695.scoutifyapp.data.api.model.GameDetails
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
import kotlin.collections.plus


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
            teamNumber = "LOADING",
            gameDetails = GameDetails(),
            teleopSection = TeleopSection.UNSTARTED
        )
    )
    val formState: StateFlow<GameFormState> = _formState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val gameDetails: GameDetails = gameDetailRepository.getGameDetailsByTaskId(taskId)
            val taskResult: Result<Task> = taskRepository.getTaskById(taskId)
            val task: Task? = taskResult.getOrNull()

            _formState.update {
                it.copy(
                    teamNumber = task?.teamNum.toString() ?: "FAILED",
                    matchNum = task?.matchNum ?: -2,
                    gameDetails = gameDetails,
                    teleopSection = if(gameDetails.teleopCompleted == true) TeleopSection.ENDED else TeleopSection.UNSTARTED
                )
            }
        }

        _formState
            .debounce(2000L)
            .distinctUntilChanged() // Only save if the state actually changed
            .onEach { currentFormState: GameFormState ->
                //save game details
                gameDetailRepository.updateDbFromGameDetails(currentFormState.gameDetails)

                //update task progress
                if(currentFormState.gameDetails.task_id != null) {
                    taskRepository.updateTaskProgress(id=currentFormState.gameDetails.task_id, progress = currentFormState.totalProgress)
                }
            }
            .launchIn(viewModelScope) // Run this in the background tied to the ViewModel's lifecycle
    }

    fun formEvent(gameDetails: GameDetails) {
        _formState.update {
            it.copy(
                gameDetails = gameDetails
            )
        }
    }
    fun getAllianceForMatch(MatchNum:Long,TeamNum: Long):String{
        return matchRepository.getAllianceForMatch(MatchNum, TeamNum)
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
}