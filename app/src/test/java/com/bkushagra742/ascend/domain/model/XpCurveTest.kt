package com.bkushagra742.ascend.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class XpCurveTest {

    @Test
    fun `level 1 requires zero cumulative xp`() {
        assertEquals(0L, XpCurve.xpRequiredForLevel(1))
    }

    @Test
    fun `xp requirement increases monotonically with level`() {
        var previous = XpCurve.xpRequiredForLevel(1)
        for (level in 2..XpCurve.MAX_LEVEL) {
            val current = XpCurve.xpRequiredForLevel(level)
            assertTrue("Level $level should require more XP than level ${level - 1}", current > previous)
            previous = current
        }
    }

    @Test
    fun `levelForCumulativeXp resolves back to the correct level at exact thresholds`() {
        val level10Threshold = XpCurve.xpRequiredForLevel(10)
        assertEquals(10, XpCurve.levelForCumulativeXp(level10Threshold))
    }

    @Test
    fun `levelForCumulativeXp returns level 1 for zero xp`() {
        assertEquals(1, XpCurve.levelForCumulativeXp(0L))
    }

    @Test
    fun `xpForNextLevel is zero at max level`() {
        assertEquals(0L, XpCurve.xpForNextLevel(XpCurve.MAX_LEVEL))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `xpRequiredForLevel rejects level 0`() {
        XpCurve.xpRequiredForLevel(0)
    }
}
