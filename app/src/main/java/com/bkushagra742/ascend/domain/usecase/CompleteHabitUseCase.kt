package com.bkushagra742.ascend.domain.usecase

import com.bkushagra742.ascend.domain.model.Habit
import com.bkushagra742.ascend.domain.model.HabitMasteryCurve
import com.bkushagra742.ascend.domain.model.HabitType
import com.bkushagra742.ascend.domain.repository.HabitRepository
import com.bkushagra742.ascend.domain.repository.PlayerRepository
import com.bkushagra742.ascend.domain.repository.StreakRepository
import javax.inject.Inject

/**
 * Completing a habit: marks it done, awards Mastery XP (per-habit leveling, see
 * HabitMasteryCurve) + attribute gains to the Player, and counts as a streak-qualifying
 * action for the global Streak Engine. Negative habits ("no smoking") use the SAME
 * completion path — for a negative habit, "complete" means "I successfully avoided it
 * today," which is exactly the behavior we want to reward (FR-HAB-02), not a different
 * code path with different rules.
 */
class CompleteHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val playerRepository: PlayerRepository,
    private val streakRepository: StreakRepository,
) {
    suspend operator fun invoke(habitId: String): Result<Habit> = try {
        val habit = habitRepository.completeHabit(habitId)

        playerRepository.applyRewards(
            xpGained = 0L, // habits feed Mastery XP, not Player XP directly, by design —
            // keeps the Player level curve driven by Quests (the primary loop) while
            // habits build their own visible mastery progress. Revisit if playtesting
            // shows habits feel under-rewarded.
            creditsGained = HABIT_COMPLETION_CREDITS,
            essenceGained = 0L,
            attributeGains = habit.attributeRewards,
        )

        streakRepository.recordQualifyingAction()

        Result.success(habit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    companion object {
        const val HABIT_COMPLETION_CREDITS = 15L
    }
}
