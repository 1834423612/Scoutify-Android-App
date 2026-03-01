package com.team695.scoutifyapp.ui.screens

import android.R.color.white
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.team695.scoutifyapp.data.api.model.Match
import com.team695.scoutifyapp.data.types.SaveStatus
import com.team695.scoutifyapp.ui.components.BackgroundGradient
import com.team695.scoutifyapp.ui.components.ImageBackground
import com.team695.scoutifyapp.ui.components.buttonHighlight
import com.team695.scoutifyapp.ui.theme.*
import com.team695.scoutifyapp.ui.reusables.Pressable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(
    viewModel: CommentsViewModel,
    matchNumber: Int?
) {
    val selectedMatch by viewModel.selectedMatch
    val red1Comment by viewModel.red1Comment
    val red2Comment by viewModel.red2Comment
    val red3Comment by viewModel.red3Comment
    val blue1Comment by viewModel.blue1Comment
    val blue2Comment by viewModel.blue2Comment
    val blue3Comment by viewModel.blue3Comment

    val autoSaved by viewModel.autoSaved
    val isSubmitted by viewModel.isSubmitted
    val saveStatus by viewModel.saveStatus

    val matches by viewModel.matches.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CommentsContent(
                selectedMatch = selectedMatch,
                red1Comment = red1Comment,
                red2Comment = red2Comment,
                red3Comment = red3Comment,
                blue1Comment = blue1Comment,
                blue2Comment = blue2Comment,
                blue3Comment = blue3Comment,
                onMatchSelected = { match -> viewModel.onMatchSelected(match) },
                onCommentChanged = { alliance, position, comment -> viewModel.onCommentChanged(alliance, position, comment) },
                isSubmitted = isSubmitted,
                autoSaved = autoSaved,
                saveStatus = saveStatus,
                matches = matches,
                onSubmit = { viewModel.setCommentsAsSubmitted() },
                printDB = { viewModel.printDB() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsContent(
    selectedMatch: String,
    red1Comment: String,
    red2Comment: String,
    red3Comment: String,
    blue1Comment: String,
    blue2Comment: String,
    blue3Comment: String,
    onMatchSelected: (String) -> Unit,
    onCommentChanged: (String, Int, String) -> Unit,
    isSubmitted: Boolean,
    autoSaved: Boolean,
    saveStatus: SaveStatus,
    matches: List<Match>,
    onSubmit: () -> Unit,
    printDB: () -> Unit
) {
    val matches_list = (1..matches.size).map { it.toString() }
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF212427))
            .border(1.dp, LightGunmetal, RoundedCornerShape(8.dp))
    ) {
        ImageBackground(x = -1445f, y = 325f)
        BackgroundGradient()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Match Comments",
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(12.dp))

                AnimatedContent(
                    targetState = Pair(saveStatus, isSubmitted),
                    transitionSpec = {
                        slideInVertically { height -> height } + fadeIn() togetherWith
                                slideOutVertically { height -> -height } + fadeOut()
                    }
                ) { (status, submitted) ->

                    val text = when {
                        status == SaveStatus.SAVING -> "Saving..."
                        status == SaveStatus.ERROR -> "Failed to save"
                        submitted -> "Submitted ✓"
                        status == SaveStatus.AUTOSAVED -> "Auto-saved locally ✓"
                        else -> "Not Submitted"
                    }

                    if (text.isNotEmpty()) {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.titleMedium,
                            color = when (status) {
                                SaveStatus.ERROR -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .background(DarkishGunmetal)
                        .buttonHighlight(4.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.width(180.dp)
                    ) {
                        OutlinedTextField(
                            value = if (selectedMatch.isEmpty()) "Select Match" else "Match $selectedMatch",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = Accent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                            ),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(DarkGunmetal)
                        ) {
                            matches_list.forEach { match ->
                                DropdownMenuItem(
                                    text = { Text("Match $match", color = TextPrimary) },
                                    onClick = {
                                        onMatchSelected(match)
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(.1f))

                Pressable(
                    onClick = onSubmit,
                    corner = 4.dp,
                    text = "Submit",
                    modifier = Modifier
                        .width(150.dp)
                        .height(55.dp)
                ) {}

                Pressable(
                    onClick = printDB,
                    corner = 4.dp,
                    text = "PrintDB (Debug)",
                    modifier = Modifier
                        .width(150.dp)
                        .height(55.dp)
                ) {}
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Content Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    // Red Alliance Column
                    // Red Alliance Column
                    AllianceColumn(
                        title = "Red Alliance",
                        titleColor = RedAlliance,
                        modifier = Modifier.weight(1f)
                    ) {
                        CommentField("Red 1", red1Comment) { onCommentChanged("Red", 1, it) }
                        CommentField("Red 2", red2Comment) { onCommentChanged("Red", 2, it) }
                        CommentField("Red 3", red3Comment) { onCommentChanged("Red", 3, it) }
                    }

                    // Blue Alliance Column
                    AllianceColumn(
                        title = "Blue Alliance",
                        titleColor = BlueAlliance,
                        modifier = Modifier.weight(1f)
                    ) {
                        CommentField("Blue 1", blue1Comment) { onCommentChanged("Blue", 1, it) }
                        CommentField("Blue 2", blue2Comment) { onCommentChanged("Blue", 2, it) }
                        CommentField("Blue 3", blue3Comment) { onCommentChanged("Blue", 3, it) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AllianceColumn(
    title: String,
    titleColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            color = titleColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp, start = 1.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content
        )
    }
}

@Composable
fun CommentField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Deselected,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 6.dp, start = 1.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp), // Fixed height for consistency
            shape = RoundedCornerShape(4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = Accent,
                unfocusedBorderColor = LightGunmetal,
                focusedContainerColor = DarkGunmetal.copy(alpha = 0.5f),
                unfocusedContainerColor = DarkGunmetal.copy(alpha = 0.3f),
                cursorColor = Accent
            ),
            placeholder = {
                Text(
                    text = "Enter comment for $label...",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        )
    }
}

/*
@Preview(showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
fun CommentsScreenPreview() {
    ScoutifyTheme {
        CommentsScreen()
    }
}
*/
