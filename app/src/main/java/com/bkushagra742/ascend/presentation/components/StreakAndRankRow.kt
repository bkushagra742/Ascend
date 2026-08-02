package com.bkushagra742.ascend.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bkushagra742.ascend.core.theme.AscendColors
import com.bkushagra742.ascend.domain.model.Rank

/** Mockup Home Dashboard: "Daily Streak" card (left) + "Current Rank" card (right). */
@Composable
fun StreakAndRankRow(
    streakDays: Int,
    rank: Rank,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(AscendColors.Graphite)
                .padding(16.dp),
        ) {
            Text("Daily Streak", style = MaterialTheme.typography.bodySmall, color = AscendColors.CoolGray)
            Text(
                text = "$streakDays Days",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = AscendColors.RoyalGold,
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(AscendColors.Graphite)
                .padding(16.dp),
        ) {
            Text("Current Rank", style = MaterialTheme.typography.bodySmall, color = AscendColors.CoolGray)
            Text(
                text = rank.displayName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = AscendColors.SoftWhite,
            )
        }
    }
}
