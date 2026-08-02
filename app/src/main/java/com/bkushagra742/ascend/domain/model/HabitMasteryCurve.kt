package com.bkushagra742.ascend.domain.model

import kotlin.math.pow
import kotlin.math.roundToLong

/**
 * Per-habit "mastery level" — the same leveling *pattern* as [XpCurve] (cumulative-XP-style
 * curve, fast early / slow late), scaled down to a smaller range since a single habit
 * shouldn't out-level the player's own profile. Every consistent completion of a habit
 * earns Mastery XP toward this curve, separate from the Player's main XP/Level.
 *
 * Why a second curve instead of reusing XpCurve directly: the two need different ranges
 * (1-100 for the player vs. 1-20 per habit) and different pacing (a habit should feel
 * "mastered" in weeks, not months) — but the underlying idea ("cumulative XP -> level,
 * fast-then-slow curve") is intentionally identical so it reads the same to the user.
 */
object HabitMasteryCurve {
    private const val BASE = 15.0
    private const val EXPONENT = 1.4
    const val MAX_MASTERY_LEVEL = 20

    fun xpRequiredForLevel(level: Int): Long {
        require(level in 1..MAX_MASTERY_LEVEL) {
            "Mastery level must be between 1 and $MAX_MASTERY_LEVEL, was $level"
        }
        if (level == 1) return 0L
        return (BASE * (level - 1).toDouble().pow(EXPONENT)).roundToLong()
    }

    fun xpForNextLevel(currentLevel: Int): Long {
        if (currentLevel >= MAX_MASTERY_LEVEL) return 0L
        return xpRequiredForLevel(currentLevel + 1) - xpRequiredForLevel(currentLevel)
    }

    fun levelForCumulativeXp(cumulativeXp: Long): Int {
        var level = 1
        while (level < MAX_MASTERY_LEVEL && cumulativeXp >= xpRequiredForLevel(level + 1)) {
            level++
        }
        return level
    }

    /** Mastery XP earned per completion — flat, since habits don't have a difficulty
     * dial like Quests do (a habit is "done" or not, not "done at Hard difficulty"). */
    const val MASTERY_XP_PER_COMPLETION = 10L
}
