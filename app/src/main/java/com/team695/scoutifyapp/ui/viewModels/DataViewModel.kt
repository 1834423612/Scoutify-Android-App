package com.team695.scoutifyapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team695.scoutifyapp.data.api.model.GameDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.team695.scoutifyapp.data.repository.GameDetailRepository
import com.team695.scoutifyapp.data.types.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.collections.plus


@OptIn(FlowPreview::class)
class DataViewModel(private val gameDetailRepository: GameDetailRepository) : ViewModel() {

    private val _formState = MutableStateFlow(GameFormState())
    val formState: StateFlow<GameFormState> = _formState.asStateFlow()

    init {
        _formState
            .debounce(2000L)
            .distinctUntilChanged() // Only save if the state actually changed
            .onEach { currentFormState: GameFormState ->
                //When saving works, use this!
                //gameDetailRepository.updateDbFromGameDetails(convertFormToGameDetails(currentFormState))
            }
            .launchIn(viewModelScope) // Run this in the background tied to the ViewModel's lifecycle
    }

    // called by UI to update match form state
    fun onEvent(event: FormEvent) {
        _formState.update { state ->
            when (event) {
                // Metadata
                is FormEvent.UpdateMetadata -> {
                    state.copy(matchId = event.matchId, teamNumber = event.teamNumber)
                }

                // Preload
                is FormEvent.UpdatePreloadDouble -> {
                    val updatedMap = state.preload.doubleFields + (event.field to event.value)
                    state.copy(preload = state.preload.copy(doubleFields = updatedMap))
                }
                is FormEvent.UpdatePreloadBool -> {
                    val updatedMap = state.preload.boolFields + (event.field to event.value)
                    state.copy(preload = state.preload.copy(boolFields = updatedMap))
                }

                // Auton
                is FormEvent.UpdateAutonString -> {
                    val updatedMap = state.auton.stringFields + (event.field to event.value)
                    state.copy(auton = state.auton.copy(stringFields = updatedMap))
                }
                is FormEvent.UpdateAutonBool -> {
                    val updatedMap = state.auton.boolFields + (event.field to event.value)
                    state.copy(auton = state.auton.copy(boolFields = updatedMap))
                }
                is FormEvent.UpdateAutonInt -> {
                    val updatedMap = state.auton.intFields + (event.field to event.value)
                    state.copy(auton = state.auton.copy(intFields = updatedMap))
                }

                // Teleop Base Fields
                is FormEvent.UpdateTeleopInt -> {
                    val updatedMap = state.teleop.intFields + (event.field to event.value)
                    state.copy(teleop = state.teleop.copy(intFields = updatedMap))
                }
                is FormEvent.UpdateTeleopBool -> {
                    val updatedMap = state.teleop.boolFields + (event.field to event.value)
                    state.copy(teleop = state.teleop.copy(boolFields = updatedMap))
                }

                // Nested Teleop Transition
                is FormEvent.UpdateTransitionInt -> {
                    // 1. Create the new map
                    val updatedMap = state.teleop.transition.intFields + (event.field to event.value)
                    // 2. Deep copy: GameFormState -> TeleopState -> TransitionState
                    state.copy(
                        teleop = state.teleop.copy(
                            transition = state.teleop.transition.copy(intFields = updatedMap)
                        )
                    )
                }
                is FormEvent.UpdateTransitionBool -> {
                    val updatedMap = state.teleop.transition.boolFields + (event.field to event.value)
                    state.copy(
                        teleop = state.teleop.copy(
                            transition = state.teleop.transition.copy(boolFields = updatedMap)
                        )
                    )
                }

                // Nested Teleop Shifts (1 through 4)
                is FormEvent.UpdateShiftInt -> {
                    when (event.shiftNumber) {
                        1 -> {
                            val updatedMap = state.teleop.shift1.intFields + (event.field to event.value)
                            state.copy(
                                teleop = state.teleop.copy(
                                    shift1 = state.teleop.shift1.copy(intFields = updatedMap)
                                )
                            )
                        }
                        2 -> {
                            val updatedMap = state.teleop.shift2.intFields + (event.field to event.value)
                            state.copy(
                                teleop = state.teleop.copy(
                                    shift2 = state.teleop.shift2.copy(intFields = updatedMap)
                                )
                            )
                        }
                        3 -> {
                            val updatedMap = state.teleop.shift3.intFields + (event.field to event.value)
                            state.copy(
                                teleop = state.teleop.copy(
                                    shift3 = state.teleop.shift3.copy(intFields = updatedMap)
                                )
                            )
                        }
                        4 -> {
                            val updatedMap = state.teleop.shift4.intFields + (event.field to event.value)
                            state.copy(
                                teleop = state.teleop.copy(
                                    shift4 = state.teleop.shift4.copy(intFields = updatedMap)
                                )
                            )
                        }
                        else -> state
                    }
                }

                // Nested Teleop Endgame
                is FormEvent.UpdateEndgameInt -> {
                    val updatedMap = state.teleop.endgame.intFields + (event.field to event.value)
                    state.copy(
                        teleop = state.teleop.copy(
                            endgame = state.teleop.endgame.copy(intFields = updatedMap)
                        )
                    )
                }
                is FormEvent.UpdateEndgameBool -> {
                    val updatedMap = state.teleop.endgame.boolFields + (event.field to event.value)
                    state.copy(
                        teleop = state.teleop.copy(
                            endgame = state.teleop.endgame.copy(boolFields = updatedMap)
                        )
                    )
                }
                is FormEvent.UpdateEndgameString -> {
                    val updatedMap = state.teleop.endgame.stringFields + (event.field to event.value)
                    state.copy(
                        teleop = state.teleop.copy(
                            endgame = state.teleop.endgame.copy(stringFields = updatedMap)
                        )
                    )
                }

                // Postgame
                is FormEvent.UpdatePostgameBool -> {
                    val updatedMap = state.postgame.boolFields + (event.field to event.value)
                    state.copy(postgame = state.postgame.copy(boolFields = updatedMap))
                }
            }

        }
    }


    suspend fun convertFormToGameDetails(gameFormState: GameFormState): GameDetails {
        //TO DO: Convert form state to game details
        return GameDetails() //Placeholder
    }
}


enum class GameSection { PRE_GAME, AUTO, TELE_OP, POST_GAME }