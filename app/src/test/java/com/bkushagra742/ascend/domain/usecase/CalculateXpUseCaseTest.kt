package com.bkushagra742.ascend.domain.usecase

import com.bkushagra742.ascend.domain.model.QuestDifficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculateXpUseCaseTest {

    private val calculateXp = CalculateXpUseCase()

    @Test
    fun `base easy quest with no bonuses returns base xp times difficulty multiplier`() {
        val result = calculateXp(
            CalculateXpUseCase.Params(baseXp = 100, difficulty = QuestDifficulty.EASY)
        )
        assertEquals(100L, result) // 100 * 1.0 * 1.0 * 1.0
    }

    @Test
    fun `hard difficulty applies its multiplier`() {
        val result = calculateXp(
            CalculateXpUseCase.Params(baseXp = 100, difficulty = QuestDifficulty.HARD)
        )
        assertEquals(220L, result) // 100 * 2.2
    }

    @Test
    fun `streak bonus increases xp proportionally to streak days`() {
        val noStreak = calculateXp(
            CalculateXpUseCase.Params(baseXp = 100, difficulty = QuestDifficulty.EASY, streakDays = 0)
        )
        val tenDayStreak = calculateXp(
            CalculateXpUseCase.Params(baseXp = 100, difficulty = QuestDifficulty.EASY, streakDays = 10)
        )
        assertTrue("10-day streak should award more XP than no streak", tenDayStreak > noStreak)
        assertEquals(120L, tenDayStreak) // 100 * (1.0 + 10*0.02) = 100 * 1.2
    }

    @Test
    fun `streak bonus is capped at MAX_STREAK_MULTIPLIER even for very long streaks`() {
        val result = calculateXp(
            CalculateXpUseCase.Params(baseXp = 100, difficulty = QuestDifficulty.EASY, streakDays = 1000)
        )
        // Cap is 2.0x — verifies a 1000-day streak doesn't produce runaway XP inflation
        assertEquals(200L, result)
    }

    @Test
    fun `boss mission applies the boss bonus multiplier`() {
        val result = calculateXp(
            CalculateXpUseCase.Params(
                baseXp = 100,
                difficulty = QuestDifficulty.BOSS,
                isBossMission = true,
            )
        )
        assertEquals(612L, result) // 100 * 3.5 * 1.75 = 612.5 -> truncated to 612
    }

    @Test
    fun `result is never less than 1 xp even with a zero base`() {
        val result = calculateXp(
            CalculateXpUseCase.Params(baseXp = 0, difficulty = QuestDifficulty.EASY)
        )
        assertEquals(1L, result)
    }

    @Test
    fun `combo multiplier is capped at MAX_COMBO_MULTIPLIER`() {
        val result = calculateXp(
            CalculateXpUseCase.Params(baseXp = 100, difficulty = QuestDifficulty.EASY, comboCount = 100)
        )
        // Cap is 1.5x
        assertEquals(150L, result)
    }
}
