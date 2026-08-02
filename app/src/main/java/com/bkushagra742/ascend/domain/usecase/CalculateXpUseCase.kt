package com.bkushagra742.ascend.domain.usecase

import com.bkushagra742.ascend.domain.model.QuestDifficulty
import javax.inject.Inject
import kotlin.math.min

/**
 * FR-XPE-01/02: all XP multiplier logic lives here and ONLY here. No feature is allowed
 * to compute XP inline — everything routes through this use case so a balance change is
 * a one-file edit and every reward source behaves consistently.
 *
 * Formula:
 *   finalXp = baseXp
 *             * difficulty.xpMultiplier
 *             * streakMultiplier(streakDays)
 *             * comboMultiplier(comboCount)
 *             * (if isBossMission) BOSS_BONUS else 1.0
 *             * (if isPrestige) PRESTIGE_BONUS else 1.0   // no-op until V2 prestige ships
 */
class CalculateXpUseCase @Inject constructor() {

    operator fun invoke(params: Params): Long {
        val streakMult = streakMultiplier(params.streakDays)
        val comboMult = comboMultiplier(params.comboCount)
        val bossMult = if (params.isBossMission) BOSS_BONUS else 1.0
        val prestigeMult = if (params.isPrestige) PRESTIGE_BONUS else 1.0

        val raw = params.baseXp *
            params.difficulty.xpMultiplier *
            streakMult *
            comboMult *
            bossMult *
            prestigeMult

        return raw.toLong().coerceAtLeast(1L)
    }

    /** Streak bonus caps at [MAX_STREAK_MULTIPLIER] so a 400-day streak doesn't produce
     * absurd numbers — grows +2% per day up to the cap. */
    private fun streakMultiplier(streakDays: Int): Double {
        val uncapped = 1.0 + (streakDays.coerceAtLeast(0) * 0.02)
        return min(uncapped, MAX_STREAK_MULTIPLIER)
    }

    /** Same-session combo bonus (e.g. completing several quests back-to-back) — smaller
     * and shorter-lived than the streak bonus, capped separately. */
    private fun comboMultiplier(comboCount: Int): Double {
        val uncapped = 1.0 + (comboCount.coerceAtLeast(0) * 0.05)
        return min(uncapped, MAX_COMBO_MULTIPLIER)
    }

    data class Params(
        val baseXp: Long,
        val difficulty: QuestDifficulty,
        val streakDays: Int = 0,
        val comboCount: Int = 0,
        val isBossMission: Boolean = false,
        val isPrestige: Boolean = false,
    )

    companion object {
        const val BOSS_BONUS = 1.75
        const val PRESTIGE_BONUS = 1.25 // unused until V2, defined now so the formula shape is final
        const val MAX_STREAK_MULTIPLIER = 2.0
        const val MAX_COMBO_MULTIPLIER = 1.5
    }
}
