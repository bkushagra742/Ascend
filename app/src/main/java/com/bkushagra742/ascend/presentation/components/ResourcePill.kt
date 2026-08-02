package com.bkushagra742.ascend.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bkushagra742.ascend.R
import com.bkushagra742.ascend.core.theme.AscendColors
import androidx.compose.foundation.Image

/**
 * Matches mockup screen 4 (Home Dashboard) top resource row: Credits / Crystals / Energy
 * pills. "Crystals" in the mockup = Essence Stone (ENS) per the locked currency decision —
 * label text uses our real naming even though the visual reference still says "Crystals".
 */
@Composable
fun ResourcePill(
    iconRes: Int,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(AscendColors.Graphite)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = AscendColors.SoftWhite,
        )
    }
}

@Composable
fun ResourceRow(
    credits: Long,
    essenceStones: Long,
    energy: Int,
    energyMax: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
    ) {
        ResourcePill(iconRes = R.drawable.ic_credits, value = formatCompact(credits))
        ResourcePill(iconRes = R.drawable.ic_essence_stone, value = formatCompact(essenceStones))
        ResourcePill(iconRes = R.drawable.ic_energy, value = "$energy/$energyMax")
    }
}

private fun formatCompact(value: Long): String =
    if (value >= 1000) "${"%.1f".format(value / 1000.0)}k" else value.toString()
