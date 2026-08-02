package com.bkushagra742.ascend.core.theme

import androidx.compose.ui.graphics.Color

/**
 * Ascend design tokens — locked in the design system decisions.
 * These are the ONLY color constants that should be referenced by name in UI code;
 * everything else derives from [AscendColorScheme] below so a future re-theme
 * (e.g. a light mode, if ever added) only touches this one file.
 */
object AscendColors {
    val ObsidianBlack = Color(0xFF0A0A0A)   // background
    val Graphite = Color(0xFF151515)        // surface
    val CrimsonRed = Color(0xFFC62828)      // primary
    val RoyalGold = Color(0xFFD4AF37)       // secondary
    val Emerald = Color(0xFF00C853)         // success
    val Amber = Color(0xFFFFB300)           // warning
    val Ruby = Color(0xFFD32F2F)            // error
    val SoftWhite = Color(0xFFF5F5F5)       // text primary
    val CoolGray = Color(0xFFA8A8A8)        // text secondary

    // Derived surface tones — not in the original brief, but needed for elevation/pressed
    // states without breaking AMOLED-black intent. Kept close to Obsidian/Graphite.
    val SurfaceElevated = Color(0xFF1E1E1E)
    val SurfacePressed = Color(0xFF232323)
    val DividerLow = Color(0xFF2A2A2A)

    // Rank tier colors — used by badge/frame rendering, kept centralized so the
    // Compose versions match the SVG asset generation script exactly.
    val RankBronze = Color(0xFF8C5A2B)
    val RankSilver = Color(0xFFB8B8B8)
    val RankGold = Color(0xFFD4AF37)
    val RankPlatinum = Color(0xFFA9C6D4)
    val RankDiamond = Color(0xFF8FD9E8)
    val RankMaster = Color(0xFFC62828)
    val RankGrandmaster = Color(0xFFC62828) // + gold accent, handled in badge composable
    val RankLegend = Color(0xFFD4AF37)      // + crimson accent, handled in badge composable
}
