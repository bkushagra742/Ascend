package com.bkushagra742.ascend.core.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bkushagra742.ascend.domain.repository.HabitRepository
import com.bkushagra742.ascend.domain.repository.QuestRepository
import com.bkushagra742.ascend.domain.repository.StreakRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * FR-QST-01, FR-HAB-01, FR-STR-02: runs once daily at the user's configured reset hour
 * (default midnight — see AscendWorkScheduler). Order matters:
 *   1. Evaluate the streak FIRST (needs yesterday's completion state, before anything resets)
 *   2. THEN reset daily Mission/Habit completion flags for the new day
 * Reversing this order would make every streak evaluation see "today" as already reset,
 * breaking the freeze-token/streak-break logic.
 */
@HiltWorker
class DailyResetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val streakRepository: StreakRepository,
    private val questRepository: QuestRepository,
    private val habitRepository: HabitRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            streakRepository.evaluateDailyRollover()
            questRepository.resetDailyQuests()
            habitRepository.resetDailyHabits()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
