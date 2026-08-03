package com.bkushagra742.ascend.presentation.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bkushagra742.ascend.core.theme.AscendColors
import com.bkushagra742.ascend.domain.model.PlayerProfile
import com.bkushagra742.ascend.presentation.components.QuestCard
import com.bkushagra742.ascend.presentation.components.ResourceRow
import com.bkushagra742.ascend.presentation.components.StreakAndRankRow
import com.bkushagra742.ascend.presentation.components.XpBar

/**
 * Mirrors mockup screen "4. HOME DASHBOARD": profile header with level + notification bell,
 * resource pill row, streak/rank cards, today's missions list, quick actions.
 * Quick Actions grid and the avatar image are wired to placeholder assets — see
 * res/drawable/README in the asset pipeline notes for exact filenames to drop real art into.
 */
@Composable
fun DashboardScreen(
    onNavigateToQuests: () -> Unit,
    onNavigateToHabits: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AscendColors.ObsidianBlack)
    ) {
        if (state.isLoading || state.profile == null) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = AscendColors.CrimsonRed,
            )
            return@Box
        }

        val profile = state.profile!!

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { DashboardHeader(profile = profile, xpProgress = state.xpProgress) }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    QuickActionButton(label = "Missions", modifier = Modifier.weight(1f), onClick = onNavigateToQuests)
                    QuickActionButton(label = "Habits", modifier = Modifier.weight(1f), onClick = onNavigateToHabits)
                }
            }
            item {
                StreakAndRankRow(
                    streakDays = state.currentStreak,
                    rank = profile.rank,
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Today's Missions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = AscendColors.SoftWhite,
                    )
                    Text(
                        "View All",
                        style = MaterialTheme.typography.labelLarge,
                        color = AscendColors.RoyalGold,
                        modifier = Modifier.clip(MaterialTheme.shapes.small)
                            .padding(4.dp),
                    )
                }
            }

            if (state.todaysMissions.isEmpty()) {
                item {
                    Text(
                        "No missions yet — add your first one to start earning XP.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AscendColors.CoolGray,
                        modifier = Modifier.padding(vertical = 24.dp),
                    )
                }
            } else {
                items(state.todaysMissions, key = { it.id }) { quest ->
                    QuestCard(
                        quest = quest,
                        onComplete = { viewModel.onCompleteQuest(quest.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(profile: PlayerProfile, xpProgress: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar placeholder — swap for the equipped avatar drawable once the
                // Vault/equip system reads profile.equippedAvatarId (next milestone).
                Image(
                    painter = painterResource(id = com.bkushagra742.ascend.R.drawable.ic_avatar_placeholder),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AscendColors.SurfaceElevated)
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                Column {
                    Text(
                        "Agent",
                        style = MaterialTheme.typography.titleMedium,
                        color = AscendColors.SoftWhite,
                    )
                    Text(
                        "Level ${profile.level}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AscendColors.CoolGray,
                    )
                }
            }
            Image(
                painter = painterResource(id = com.bkushagra742.ascend.R.drawable.ic_notification_bell),
                contentDescription = "Notifications",
                modifier = Modifier.size(28.dp),
            )
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
        XpBar(progress = xpProgress)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(6.dp))
        Text(
            "${profile.currentXp} XP",
            style = MaterialTheme.typography.bodySmall,
            color = AscendColors.CoolGray,
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
        ResourceRow(
            credits = profile.credits,
            essenceStones = profile.essenceStones,
            energy = 110, // Energy system not implemented yet — static per mockup until then
            energyMax = 110,
        )
    }
}

@Composable
private fun QuickActionButton(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(AscendColors.Graphite)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = AscendColors.SoftWhite,
        )
    }
}
