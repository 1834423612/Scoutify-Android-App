package com.team695.scoutifyapp.ui.screens.data

import android.os.Parcelable
import android.webkit.RenderProcessGoneDetail
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.team695.scoutifyapp.ui.viewModels.DataViewModel
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.team695.scoutifyapp.R
import com.team695.scoutifyapp.data.types.ENDGAME_END_TIME
import com.team695.scoutifyapp.data.types.GameFormState
import com.team695.scoutifyapp.data.types.GameSection
import com.team695.scoutifyapp.data.types.SHIFT1_END_TIME
import com.team695.scoutifyapp.data.types.SHIFT2_END_TIME
import com.team695.scoutifyapp.data.types.SHIFT3_END_TIME
import com.team695.scoutifyapp.data.types.SHIFT4_END_TIME
import com.team695.scoutifyapp.data.types.SectionType
import com.team695.scoutifyapp.data.types.TRANSITION_END_TIME
import com.team695.scoutifyapp.data.types.TeleopSection
import com.team695.scoutifyapp.ui.components.progressBorder
import com.team695.scoutifyapp.ui.components.buttonHighlight
import com.team695.scoutifyapp.ui.components.BackgroundGradient
import com.team695.scoutifyapp.ui.components.ImageBackground
import com.team695.scoutifyapp.ui.theme.DarkGunmetal
import com.team695.scoutifyapp.ui.theme.DarkishGunmetal
import com.team695.scoutifyapp.ui.theme.Deselected
import com.team695.scoutifyapp.ui.theme.LightGunmetal
import com.team695.scoutifyapp.ui.theme.TextPrimary
import com.team695.scoutifyapp.ui.theme.mediumCornerRadius
import com.team695.scoutifyapp.ui.theme.smallCornerRadius
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize



@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun DataScreen(
    navController: NavHostController,
    dataViewModel: DataViewModel
) {
    val formState: GameFormState by dataViewModel.formState.collectAsStateWithLifecycle()

    val sections: List<GameSection> = arrayOf(
        SectionType.PREGAME, SectionType.AUTON, SectionType.TELEOP, SectionType.POSTGAME)
        .map { s: SectionType -> GameSection(type = s, progress = 0f) }
    
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val customDirective = calculatePaneScaffoldDirective(adaptiveInfo).copy(
        horizontalPartitionSpacerSize = 8.dp //change default gap between list and detail panes
    )
    val navigator = rememberListDetailPaneScaffoldNavigator<GameSection>(
        //set material library default gap from 24.dp to 8.dp
        scaffoldDirective = customDirective,
        //make the list show Pre-game by default. This is necessary because the list assumes an item is always selected
        initialDestinationHistory = listOf(
            ThreePaneScaffoldDestinationItem(ListDetailPaneScaffoldRole.List),
            ThreePaneScaffoldDestinationItem(ListDetailPaneScaffoldRole.Detail, sections.first())
        )
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(formState.teleopRunning) {
        var startTime: Long = withFrameMillis { it }

        while(formState.teleopRunning) {
            withFrameMillis { frameTimeMillis ->
                val deltaTime = (frameTimeMillis-startTime).toInt()
                val switchTime: Int
                val nextSection: TeleopSection
                when(formState.teleopSection) {
                    TeleopSection.TRANSITION -> {
                        switchTime = TRANSITION_END_TIME
                        nextSection = TeleopSection.SHIFT1
                    }
                    TeleopSection.SHIFT1 -> {
                        switchTime = SHIFT1_END_TIME
                        nextSection = TeleopSection.SHIFT2
                    }
                    TeleopSection.SHIFT2 -> {
                        switchTime = SHIFT2_END_TIME
                        nextSection = TeleopSection.SHIFT3
                    }
                    TeleopSection.SHIFT3 -> {
                        switchTime = SHIFT3_END_TIME
                        nextSection = TeleopSection.SHIFT4
                    }
                    TeleopSection.SHIFT4 -> {
                        switchTime = SHIFT4_END_TIME
                        nextSection = TeleopSection.ENDGAME
                    }
                    TeleopSection.ENDGAME -> {
                        switchTime = ENDGAME_END_TIME
                        nextSection = TeleopSection.ENDED
                    }
                    else -> {
                        switchTime = Int.MAX_VALUE
                        nextSection = TeleopSection.ENDED
                    }
                }

                val newTime = formState.teleopTotalMilliseconds + deltaTime
                if (newTime > switchTime) {
                    if(nextSection == TeleopSection.ENDED) {
                        dataViewModel.endTeleop()
                    }
                    dataViewModel.setTeleopSection(teleopSection = nextSection, teleopTotalMilliseconds = switchTime)
                }
                dataViewModel.updateTime(deltaTime = (frameTimeMillis-startTime).toInt())
                startTime = frameTimeMillis
            }
        }
    }



    Row(
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SharedTransitionLayout {
            NavigableListDetailPaneScaffold(
                navigator = navigator,
                listPane = {
                    AnimatedPane (
                        modifier = Modifier.preferredWidth(250.dp) // Make list smaller (or use 0.3f for 30% width)
                    ) {
                        ListContent(
                            sections = sections,
                            selectedSection = navigator.currentDestination?.contentKey,
                            onSectionClick = { section: GameSection ->
                                scope.launch {
                                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, section)
                                }
                            },
                            animatedVisibilityScope = this@AnimatedPane,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            formState = formState,
                            dataViewModel = dataViewModel
                        )
                    }
                },
                detailPane = {
                    AnimatedPane {
                        DetailContent(
                            section = navigator.currentDestination?.contentKey,
                            animatedVisibilityScope = this@AnimatedPane,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            onClosePane = {
                                scope.launch {
                                    navigator.navigateBack(
                                        backNavigationBehavior = BackNavigationBehavior.PopUntilScaffoldValueChange,
                                    )

                                }
                            },
                            formState = formState,
                            dataViewModel = dataViewModel
                        )
                    }
                }
            )
        }
        TeleopLockScreen(isOverlayActive = formState.teleopRunning)
    }
}

@Composable
private fun ListContent(
    sections: List<GameSection>,
    selectedSection: GameSection?,
    onSectionClick: (section: GameSection) -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    formState: GameFormState,
    dataViewModel: DataViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(smallCornerRadius))

    ) {
        ImageBackground(x = -350f, y = 330f)
        BackgroundGradient()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            //Game Section top text

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Game Sections",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(8.dp))

            HorizontalDivider(color = Deselected, thickness = 1.dp)

            Spacer(Modifier.height(16.dp))


            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = modifier.selectableGroup()
            ) {
                itemsIndexed(sections) { index: Int, section: GameSection ->

                    var isFlagged: Boolean = false

                    when(section.type) {
                        SectionType.PREGAME -> {
                            isFlagged = formState.gameDetails.pregameFlag == true //use == because flag could be null
                        }
                        SectionType.AUTON -> {
                            isFlagged = formState.gameDetails.autonFlag == true
                        }
                        SectionType.TELEOP -> {
                            isFlagged = formState.gameDetails.teleopFlag == true
                        }
                        SectionType.POSTGAME -> {
                            isFlagged = formState.gameDetails.postgameFlag == true
                        }
                    }


                    val containerColor =
                        if (sections[index] == selectedSection) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                    val borderStroke =
                        if (sections[index] == selectedSection) null else BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline
                        )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .progressBorder(progress=section.progress)
                            .background(color = DarkGunmetal, shape = RoundedCornerShape(mediumCornerRadius))
                            .clip(RoundedCornerShape(mediumCornerRadius))
                            .buttonHighlight(
                                corner = smallCornerRadius
                            )
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                            .clickable {
                                onSectionClick(section)
                            }
                        ,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(smallCornerRadius))
                                .background(DarkishGunmetal)
                                .fillMaxWidth()
                                .buttonHighlight(
                                    corner = smallCornerRadius
                                )
                                .padding(10.dp,0.dp,10.dp,0.dp)
                        ) {
                            Text(
                                text = section.name,
                                color = TextPrimary,
                                fontSize = 20.sp,
                            )
                            Image(
                                painter = painterResource(id = if (isFlagged) R.drawable.flag_selected else R.drawable.flag_deselected),
                                contentDescription = "Time",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable {
                                        //use != because flag could be null
                                        when(section.type) {
                                            SectionType.PREGAME -> {
                                                dataViewModel.formEvent(
                                                    gameDetails = formState.gameDetails.copy(pregameFlag = formState.gameDetails.pregameFlag != true)
                                                )
                                            }
                                            SectionType.AUTON -> {
                                                dataViewModel.formEvent(
                                                    gameDetails = formState.gameDetails.copy(autonFlag = formState.gameDetails.autonFlag != true)
                                                )
                                            }
                                            SectionType.TELEOP -> {
                                                dataViewModel.formEvent(
                                                    gameDetails = formState.gameDetails.copy(teleopFlag = formState.gameDetails.teleopFlag != true)
                                                )
                                            }
                                            SectionType.POSTGAME -> {
                                                dataViewModel.formEvent(
                                                    gameDetails = formState.gameDetails.copy(postgameFlag = formState.gameDetails.postgameFlag != true)
                                                )
                                            }
                                        }
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun DetailContent(
    section: GameSection?,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    formState: GameFormState,
    dataViewModel: DataViewModel,
    onClosePane: () -> Unit
) {

    Column (
        modifier = modifier
            .fillMaxSize()
            .fillMaxWidth()
            .background(Color(0xFF000000))
            .clip(RoundedCornerShape(smallCornerRadius))
            .border(1.dp, LightGunmetal, RoundedCornerShape(smallCornerRadius)),
        verticalArrangement = Arrangement.Top

    ) {
        ImageBackground(x = -1350f, y = 355f)
        BackgroundGradient()

        WarningModal (
            visible   = formState.showWarningModal,

            onContinue = {
                dataViewModel.toggleWarningModal(title = "", text = "")
                when(formState.teleopSection) {
                    TeleopSection.UNSTARTED -> {
                        dataViewModel.startTeleop()
                    }
                    TeleopSection.TRANSITION -> {
                        dataViewModel.setTeleopSection(teleopSection = TeleopSection.SHIFT1, teleopTotalMilliseconds = TRANSITION_END_TIME)
                    }
                    TeleopSection.SHIFT1 -> {
                        dataViewModel.setTeleopSection(teleopSection = TeleopSection.SHIFT2, teleopTotalMilliseconds = SHIFT1_END_TIME)
                    }
                    TeleopSection.SHIFT2 -> {
                        dataViewModel.setTeleopSection(teleopSection = TeleopSection.SHIFT3, teleopTotalMilliseconds = SHIFT2_END_TIME)
                    }
                    TeleopSection.SHIFT3 -> {
                        dataViewModel.setTeleopSection(teleopSection = TeleopSection.SHIFT4, teleopTotalMilliseconds = SHIFT3_END_TIME)
                    }
                    TeleopSection.SHIFT4 -> {
                        dataViewModel.setTeleopSection(teleopSection = TeleopSection.ENDGAME, teleopTotalMilliseconds = SHIFT4_END_TIME)
                    }
                    TeleopSection.ENDGAME, TeleopSection.ENDED -> {
                        dataViewModel.startTeleop()
                    }
                }
            },

            onCancel = {
                dataViewModel.toggleWarningModal(title = "", text = "")
            },

            formState = formState
        )

        if (section != null) {
            with(sharedTransitionScope) {
                //animated stuff goes here
                when(section.type) {
                    SectionType.PREGAME -> {
                        PregameDetails(
                            dataViewModel = dataViewModel,
                            formState = formState
                        )
                    }
                    SectionType.AUTON -> {

                    }
                    SectionType.TELEOP -> {
                        when(formState.teleopSection) {
                            TeleopSection.UNSTARTED -> {
                                StoppedDetails(
                                    dataViewModel = dataViewModel,
                                    formState = formState
                                )
                            }
                            TeleopSection.TRANSITION -> {
                                TransitionDetails(
                                    dataViewModel = dataViewModel,
                                    formState = formState
                                )
                            }
                            TeleopSection.SHIFT1 -> {
                                Shift1Details(
                                    dataViewModel = dataViewModel,
                                    formState = formState
                                )
                            }
                            TeleopSection.SHIFT2 -> {
                                Shift2Details(
                                    dataViewModel = dataViewModel,
                                    formState = formState
                                )
                            }
                            TeleopSection.SHIFT3 -> {
                                Shift3Details(
                                    dataViewModel = dataViewModel,
                                    formState = formState
                                )
                            }
                            TeleopSection.SHIFT4 -> {
                                Shift4Details(
                                    dataViewModel = dataViewModel,
                                    formState = formState
                                )
                            }
                            TeleopSection.ENDGAME, TeleopSection.ENDED -> {
                                EndgameDetails(
                                    dataViewModel = dataViewModel,
                                    formState = formState
                                )
                            }
                        }
                    }
                    SectionType.POSTGAME -> {
                        PostgameDetails(
                            dataViewModel = dataViewModel,
                            formState = formState
                        )
                    }
                }
            }


        } else {
            Text(
                text = "There is a bug!",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

