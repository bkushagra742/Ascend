package com.bkushagra742.ascend.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.align
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.bkushagra742.ascend.core.theme.AscendColors

/**
 * XP progress bar — mirrors the generated xp_bar_empty/fill SVG assets but as real
 * Compose (so it can actually animate on gain, per FR-XPE / level-up feedback). The
 * SVG versions remain useful as static previews for design handoff, not runtime UI.
 */
@Composable
fun XpBar(
    progress: Float, // 0f..1f
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 24.dp,
) {
    var animatedProgress by remember { mutableFloatStateOf(0f) }
    val displayed by animateFloatAsState(
        targetValue = animatedProgress,
        animationSpec = tween(durationMillis = 600),
        label = "xpBarProgress",
    )

    LaunchedEffect(progress) {
        animatedProgress = progress.coerceIn(0f, 1f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(AscendColors.Graphite)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = displayed.coerceIn(0f, 1f))
                .align(Alignment.CenterStart)
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(AscendColors.CrimsonRed, AscendColors.RoyalGold)
                    )
                )
        )
    }
}
