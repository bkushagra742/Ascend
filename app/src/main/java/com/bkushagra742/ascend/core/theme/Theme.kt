package com.bkushagra742.ascend.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * Ascend ships AMOLED dark only for V1 (per design brief — "Dark Theme, AMOLED Friendly").
 * No light color scheme is defined on purpose: a half-designed light mode would violate
 * more of the brand brief than it would satisfy, so it's deferred rather than faked.
 */
private val AscendDarkColorScheme = darkColorScheme(
    primary = AscendColors.CrimsonRed,
    onPrimary = AscendColors.SoftWhite,
    secondary = AscendColors.RoyalGold,
    onSecondary = AscendColors.ObsidianBlack,
    background = AscendColors.ObsidianBlack,
    onBackground = AscendColors.SoftWhite,
    surface = AscendColors.Graphite,
    onSurface = AscendColors.SoftWhite,
    surfaceVariant = AscendColors.SurfaceElevated,
    onSurfaceVariant = AscendColors.CoolGray,
    error = AscendColors.Ruby,
    onError = AscendColors.SoftWhite,
    outline = AscendColors.DividerLow,
)

@Composable
fun AscendTheme(
    // isSystemInDarkTheme() kept for API shape consistency even though V1 is dark-only;
    // makes it a one-line change if light mode is ever added in V2+.
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AscendDarkColorScheme,
        typography = AscendTypography,
        content = content
    )
}
