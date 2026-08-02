package com.bkushagra742.ascend.presentation.quests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bkushagra742.ascend.core.theme.AscendColors
import com.bkushagra742.ascend.presentation.components.QuestCard

/** Mirrors mockup screen "5. DAILY MISSIONS" — full list with a back button + FAB to
 * create a new Mission with a chosen difficulty. No filter tabs (All/In Progress/
 * Completed) yet; that's a small addition once this base view is confirmed. */
@Composable
fun QuestsScreen(
    onBack: () -> Unit,
    onAddMission: () -> Unit,
    viewModel: QuestsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = AscendColors.ObsidianBlack,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddMission,
                containerColor = AscendColors.CrimsonRed,
                contentColor = AscendColors.SoftWhite,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Mission")
            }
        },
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AscendColors.ObsidianBlack)
                .padding(scaffoldPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = AscendColors.SoftWhite,
                            )
                        }
                        Text(
                            "Daily Missions",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = AscendColors.SoftWhite,
                        )
                    }
                }

                if (state.isLoading) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            color = AscendColors.CrimsonRed,
                        )
                    }
                } else if (state.missions.isEmpty()) {
                    item {
                        Text(
                            "No missions yet — tap + to create your first one.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AscendColors.CoolGray,
                            modifier = Modifier.padding(24.dp),
                        )
                    }
                } else {
                    items(state.missions, key = { it.id }) { quest ->
                        QuestCard(
                            quest = quest,
                            onComplete = { viewModel.onCompleteQuest(quest.id) },
                        )
                    }
                }
            }
        }
    }
}
