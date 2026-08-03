package com.bkushagra742.ascend.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bkushagra742.ascend.core.theme.AscendColors
import com.bkushagra742.ascend.domain.model.QuestDifficulty

/** Easy/Medium/Hard/Boss picker for custom Mission creation — this is the "grant XP add
 * or choice" mechanic: picking a tier changes the XP/Credit preview live via
 * QuestDifficulty.xpMultiplier, no separate config needed per difficulty. */
@Composable
fun DifficultySelector(
    selected: QuestDifficulty,
    onSelect: (QuestDifficulty) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        QuestDifficulty.entries.forEach { difficulty ->
            val isSelected = difficulty == selected
            Text(
                text = difficulty.label(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) AscendColors.ObsidianBlack else difficulty.accentColor(),
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(if (isSelected) difficulty.accentColor() else AscendColors.Graphite)
                    .clickable { onSelect(difficulty) }
                    .padding(vertical = 10.dp),
            )
        }
    }
}

private fun QuestDifficulty.label(): String = when (this) {
    QuestDifficulty.EASY -> "Easy"
    QuestDifficulty.MEDIUM -> "Medium"
    QuestDifficulty.HARD -> "Hard"
    QuestDifficulty.BOSS -> "Boss"
}

private fun QuestDifficulty.accentColor(): Color = when (this) {
    QuestDifficulty.EASY -> AscendColors.Emerald
    QuestDifficulty.MEDIUM -> AscendColors.Amber
    QuestDifficulty.HARD -> AscendColors.CrimsonRed
    QuestDifficulty.BOSS -> AscendColors.RoyalGold
}
