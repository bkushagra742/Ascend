package com.bkushagra742.ascend.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HabitMasteryCurveTest {

    @Test
    fun `level 1 requires zero mastery xp`() {
        assertEquals(0L, HabitMasteryCurve.xpRequiredForLevel(1))
    }

    @Test
    fun `mastery requirement increases monotonically`() {
        var previous = HabitMasteryCurve.xpRequiredForLevel(1)
        for (level in 2..HabitMasteryCurve.MAX_MASTERY_LEVEL) {
            val current = HabitMasteryCurve.xpRequiredForLevel(level)
            assertTrue(current > previous)
            previous = current
        }
    }

    @Test
    fun `a habit reaches mastery level 2 within a reasonable number of completions`() {
        // Sanity check on pacing: mastery should feel achievable in roughly 1-2 weeks of
        // daily completions, not months (PRD intent: habits feel "mastered" quickly).
        val completionsNeeded = HabitMasteryCurve.xpRequiredForLevel(2) / HabitMasteryCurve.MASTERY_XP_PER_COMPLETION
        assertTrue("Expected under 10 completions to reach level 2, was $completionsNeeded", completionsNeeded < 10)
    }

    @Test
    fun `levelForCumulativeXp caps at MAX_MASTERY_LEVEL`() {
        val hugeXp = 10_000_000L
        assertEquals(HabitMasteryCurve.MAX_MASTERY_LEVEL, HabitMasteryCurve.levelForCumulativeXp(hugeXp))
    }
}
