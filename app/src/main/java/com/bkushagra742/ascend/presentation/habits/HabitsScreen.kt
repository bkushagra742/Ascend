package com.bkushagra742.ascend.presentation.habits

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bkushagra742.ascend.core.theme.AscendColors
import com.bkushagra742.ascend.presentation.components.HabitCard

/** Mirrors mockup screen "10. HABIT TRACKER" — habit list + streak/freeze-token summary
 * at the top, a FAB to add a new habit (good or bad-to-break), and a Focus Lock entry
 * point in the top bar. (The mockup's weekly M-T-W-T-F-S-S dot grid isn't wired to real
 * per-day history yet; that needs a completion-history table, a follow-up milestone.) */
@Composable
fun HabitsScreen(
    onBack: () -> Unit,
    onAddHabit: () -> Unit,
    onOpenFocusLock: () -> Unit,
    viewModel: HabitsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = AscendColors.ObsidianBlack,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddHabit,
                containerColor = AscendColors.CrimsonRed,
                contentColor = AscendColors.SoftWhite,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Habit")
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = AscendColors.SoftWhite,
                                )
                            }
                            Text(
                                "Habits",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = AscendColors.SoftWhite,
                            )
                        }
                        IconButton(onClick = onOpenFocusLock) {
                            Icon(
                                imageVector = Icons.Filled.Shield,
                                contentDescription = "Focus Lock settings",
                                tint = AscendColors.RoyalGold,
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(AscendColors.Graphite)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        StreakSummaryColumn(label = "Daily Streak", value = "${state.streak.currentStreak}")
                        StreakSummaryColumn(label = "Longest", value = "${state.streak.longestStreak}")
                        StreakSummaryColumn(label = "Streak Savers", value = "${state.streak.freezeTokensAvailable}")
                    }
                }

                if (state.isLoading) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            color = AscendColors.CrimsonRed,
                        )
                    }
                } else if (state.habits.isEmpty()) {
                    item {
                        Text(
                            "No habits yet — tap + to add a good habit, or one to break.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AscendColors.CoolGray,
                            modifier = Modifier.padding(24.dp),
                        )
                    }
                } else {
                    items(state.habits, key = { it.id }) { habit ->
                        HabitCard(
                            habit = habit,
                            onToggleComplete = { viewModel.onToggleHabit(habit) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StreakSummaryColumn(label: String, value: String) {
    androidx.compose.foundation.layout.Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, style = MaterialTheme.typography.headlineMedium, color = AscendColors.RoyalGold, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = AscendColors.CoolGray)
    }
}
