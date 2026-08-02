package com.bkushagra742.ascend.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bkushagra742.ascend.core.theme.AscendColors
import com.bkushagra742.ascend.domain.model.Habit
import com.bkushagra742.ascend.domain.model.HabitType

/**
 * Mirrors mockup screen "10. HABIT TRACKER" row style: title, weekly dot strip (not yet
 * wired to real per-day history — see TODO below), streak count, mastery level. Negative
 * habits (FR-HAB-02) get a distinct label ("Avoided today" vs "Done today") so the UI
 * never frames "not doing a bad thing" the same way as "doing a good thing" by accident.
 */
@Composable
fun HabitCard(
    habit: Habit,
    onToggleComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AscendColors.Graphite)
            .clickable(onClick = onToggleComplete)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                habit.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = AscendColors.SoftWhite,
            )
            Text(
                text = completionLabel(habit) + " · Mastery Lv.${habit.masteryLevel}",
                style = MaterialTheme.typography.bodySmall,
                color = AscendColors.CoolGray,
            )
            if (habit.currentStreak > 0) {
                Text(
                    text = "🔥 ${habit.currentStreak} day streak",
                    style = MaterialTheme.typography.labelMedium,
                    color = AscendColors.RoyalGold,
                )
            }
        }

        val checkBg = if (habit.isCompletedToday) AscendColors.Emerald else AscendColors.SurfaceElevated
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(checkBg)
                .padding(8.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = if (habit.isCompletedToday) "Completed" else "Mark complete",
                tint = if (habit.isCompletedToday) AscendColors.ObsidianBlack else AscendColors.CoolGray,
            )
        }
    }
}

private fun completionLabel(habit: Habit): String = when (habit.type) {
    HabitType.POSITIVE -> if (habit.isCompletedToday) "Done today" else "Not done yet"
    HabitType.NEGATIVE -> if (habit.isCompletedToday) "Avoided today" else "Not logged yet"
}
