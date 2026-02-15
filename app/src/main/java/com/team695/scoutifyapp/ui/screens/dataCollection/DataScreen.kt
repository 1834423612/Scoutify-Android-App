package com.team695.scoutifyapp.ui.screens.dataCollection

import android.os.Parcelable
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.team695.scoutifyapp.ui.components.progressBorder
import com.team695.scoutifyapp.ui.modifier.buttonHighlight
import com.team695.scoutifyapp.ui.reusables.BackgroundGradient
import com.team695.scoutifyapp.ui.reusables.ImageBackground
import com.team695.scoutifyapp.ui.theme.DarkGunmetal
import com.team695.scoutifyapp.ui.theme.LightGunmetal
import com.team695.scoutifyapp.ui.theme.mediumCornerRadius
import com.team695.scoutifyapp.ui.theme.smallCornerRadius
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize


@Parcelize
data class GameSection(
    val name: String,
    var progress: Float = 0f,
) : Parcelable

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun DataScreen(
    navController: NavHostController,
    dataViewModel: DataViewModel
) {
    val sections: List<GameSection> = arrayOf("Pre-game", "Autonomous", "Tele-op", "Post-game").map { s: String -> GameSection(name = s, progress = 0f) }

        val navigator = rememberListDetailPaneScaffoldNavigator<GameSection>(
        //make the list show Pre-game by default. This is necessary because the list assumes an item is always selected
        initialDestinationHistory = listOf(
            ThreePaneScaffoldDestinationItem(ListDetailPaneScaffoldRole.List),
            ThreePaneScaffoldDestinationItem(ListDetailPaneScaffoldRole.Detail, sections.first())
        )
    )
    val scope = rememberCoroutineScope()


    Row(
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SharedTransitionLayout {
            NavigableListDetailPaneScaffold(
                navigator = navigator,
                listPane = {
                    AnimatedPane {
                        ListContent(
                            sections = sections,
                            selectedSection = navigator.currentDestination?.contentKey,
                            onSectionClick = { section: GameSection ->
                                scope.launch {
                                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, section)
                                }
                            },
                            animatedVisibilityScope = this@AnimatedPane,
                            sharedTransitionScope = this@SharedTransitionLayout
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
                                        backNavigationBehavior = BackNavigationBehavior.PopUntilScaffoldValueChange
                                    )

                                }
                            }
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun ListContent(
    sections: List<GameSection>,
    selectedSection: GameSection?,
    onSectionClick: (section: GameSection) -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, LightGunmetal, RoundedCornerShape(smallCornerRadius))

    ) {
        ImageBackground(x = -350f, y = 330f)
        BackgroundGradient()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Game Sections",
                style = MaterialTheme.typography.headlineMedium
            )

            LazyColumn(
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = modifier.selectableGroup()
            ) {
                itemsIndexed(sections) { index: Int, section: GameSection ->

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
                            .height(45.dp)
                            .progressBorder(progress=section.progress)
                            .background(color = DarkGunmetal, shape = RoundedCornerShape(mediumCornerRadius))
                            .clip(RoundedCornerShape(mediumCornerRadius))
                            .buttonHighlight(
                                corner = smallCornerRadius
                            )
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "List Content",
                            style = MaterialTheme.typography.headlineMedium
                        )
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
    onClosePane: () -> Unit
) {

    Column (
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .clip(RoundedCornerShape(smallCornerRadius))
            .border(1.dp, LightGunmetal, RoundedCornerShape(smallCornerRadius))

    ) {
        ImageBackground(x = -1350f, y = 355f)
        BackgroundGradient()

        if (section != null) {
            IconButton(
                modifier =  Modifier.align(Alignment.End).padding(16.dp),
                onClick = onClosePane
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }

            with(sharedTransitionScope) {
                //animated stuff goes here
                Text(
                    text = section.name,
                    style = MaterialTheme.typography.headlineMedium
                )
            }


        } else {
            Text(
                text = "There is a bug!",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}