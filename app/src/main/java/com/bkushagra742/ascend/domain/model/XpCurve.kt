package com.bkushagra742.ascend.domain.model

import kotlin.math.pow
import kotlin.math.roundToLong

/**
 * Single source of truth for the Level 1-100 XP curve (FR-PROF-03: fast early, slow late).
 *
 * Formula: xpForLevel(n) = BASE * n^EXPONENT
 * This is a draft curve, not a final balance pass — PRD §8 flags XP economy tuning as an
 * open risk. Keeping it as one small pure function (rather than a hardcoded 100-row table)
 * means balance changes are a one-line edit, not a data migration.
 */
object XpCurve {
    private const val BASE = 80.0
    private const val EXPONENT = 1.55
    const val MAX_LEVEL = 100

    /** Total cumulative XP required to REACH [level] (level 1 = 0 XP). */
    fun xpRequiredForLevel(level: Int): Long {
        require(level in 1..MAX_LEVEL) { "Level must be between 1 and $MAX_LEVEL, was $level" }
        if (level == 1) return 0L
        return (BASE * (level - 1).toDouble().pow(EXPONENT)).roundToLong()
    }

    /** XP needed within the current level to reach the next one. */
    fun xpForNextLevel(currentLevel: Int): Long {
        if (currentLevel >= MAX_LEVEL) return 0L
        return xpRequiredForLevel(currentLevel + 1) - xpRequiredForLevel(currentLevel)
    }

    /** Resolves what level a given cumulative XP total corresponds to. */
    fun levelForCumulativeXp(cumulativeXp: Long): Int {
        var level = 1
        while (level < MAX_LEVEL && cumulativeXp >= xpRequiredForLevel(level + 1)) {
            level++
        }
        return level
    }
}
