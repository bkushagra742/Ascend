package com.bkushagra742.ascend.domain.model

/**
 * A recurring (or custom-schedule) real-life habit. Positive habits reward completion;
 * negative habits reward *avoidance* (FR-HAB-02) — see [HabitType].
 *
 * [masteryXp]/[masteryLevel] use [HabitMasteryCurve] — the per-habit "leveling" system,
 * separate from the Player's own Level/XP.
 */
data class Habit(
    val id: String,
    val title: String,
    val description: String,
    val type: HabitType,
    val recurrence: HabitRecurrence,
    val attributeRewards: Map<AttributeType, Int>,
    val isCompletedToday: Boolean,
    val currentStreak: Int,
    val longestStreak: Int,
    val masteryXp: Long,
    val chainId: String?, // FR-HAB-03: non-null if part of a Habit Chain
) {
    val masteryLevel: Int get() = HabitMasteryCurve.levelForCumulativeXp(masteryXp)

    val masteryProgress: Float
        get() {
            val currentLevelXp = HabitMasteryCurve.xpRequiredForLevel(masteryLevel)
            val neededForNext = HabitMasteryCurve.xpForNextLevel(masteryLevel).coerceAtLeast(1)
            return ((masteryXp - currentLevelXp).toFloat() / neededForNext.toFloat()).coerceIn(0f, 1f)
        }
}

/** FR-HAB-03: Habit Chains link 2+ habits; completing all of them same-day grants a bonus. */
data class HabitChain(
    val id: String,
    val name: String,
    val habitIds: List<String>,
    val bonusCredits: Long,
    val bonusXp: Long,
)
