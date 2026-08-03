package com.bkushagra742.ascend.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bkushagra742.ascend.domain.model.Quest
import com.bkushagra742.ascend.core.theme.AscendColors

/**
 * Single Quest row — used on both Dashboard (today's Missions preview) and the full
 * Quests screen, so behavior/visuals only need to be right in one place.
 */
@Composable
fun QuestCard(
    quest: Quest,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AscendColors.Graphite)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = quest.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = AscendColors.SoftWhite,
            )
            Text(
                text = "+${quest.baseXp} XP  •  +${quest.creditReward} Credits",
                style = MaterialTheme.typography.bodySmall,
                color = AscendColors.CoolGray,
            )
        }

        if (quest.isCompletedToday) {
            Text(
                text = "✓ Done",
                style = MaterialTheme.typography.labelLarge,
                color = AscendColors.Emerald,
            )
        } else {
            Button(
                onClick = onComplete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AscendColors.CrimsonRed,
                    contentColor = AscendColors.SoftWhite,
                ),
            ) {
                Text("Complete")
            }
        }
    }
}
