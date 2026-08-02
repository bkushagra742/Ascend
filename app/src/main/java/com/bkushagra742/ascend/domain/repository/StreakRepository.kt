package com.bkushagra742.ascend.domain.repository

import com.bkushagra742.ascend.domain.model.StreakState
import kotlinx.coroutines.flow.Flow

interface StreakRepository {
    fun observeStreak(): Flow<StreakState>
    suspend fun getStreak(): StreakState

    /** Called whenever a streak-qualifying action completes (a Mission, by default —
     * FR-STR-01). Idempotent within a day: completing 5 missions today only advances
     * the streak once. */
    suspend fun recordQualifyingAction()

    /**
     * Called once daily by [com.bkushagra742.ascend.core.work.DailyResetWorker] at
     * rollover. If yesterday wasn't qualified:
     *   - if a Streak Saver is available, consume one and preserve the streak (FR-STR-02)
     *   - otherwise, reset currentStreak to 0
     * Returns true if the streak survived (either qualified or saved by a token).
     */
    suspend fun evaluateDailyRollover(): Boolean

    suspend fun addFreezeTokens(count: Int)
    suspend fun getFreezeTokenCount(): Int
}
