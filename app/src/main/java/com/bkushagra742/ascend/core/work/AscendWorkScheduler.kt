package com.bkushagra742.ascend.core.work

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single place that owns "when do our background jobs run." Called once from
 * AscendApplication.onCreate(). Uses KEEP (not REPLACE) for uniqueness so re-launching
 * the app doesn't reset an already-scheduled job's timing — only a fresh install schedules
 * from scratch.
 *
 * Reminder/reset TIMES here (20:00 for streak warning, midnight for reset) are defaults —
 * FR-QST-01 calls for these to be user-configurable via Settings. That's a small follow-up
 * (read from DataStore instead of the hardcoded LocalTime below); the WorkManager wiring
 * itself doesn't change shape when that lands.
 */
@Singleton
class AscendWorkScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun scheduleAll() {
        scheduleDailyReset()
        scheduleStreakReminder()
        scheduleWeeklyReset()
    }

    private fun scheduleDailyReset() {
        val request = PeriodicWorkRequestBuilder<DailyResetWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delayUntil(LocalTime.MIDNIGHT), TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_DAILY_RESET, ExistingPeriodicWorkPolicy.KEEP, request,
        )
    }

    private fun scheduleStreakReminder() {
        val request = PeriodicWorkRequestBuilder<StreakReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delayUntil(DEFAULT_STREAK_REMINDER_TIME), TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_STREAK_REMINDER, ExistingPeriodicWorkPolicy.KEEP, request,
        )
    }

    private fun scheduleWeeklyReset() {
        val request = PeriodicWorkRequestBuilder<WeeklyResetWorker>(7, TimeUnit.DAYS)
            .setInitialDelay(delayUntilNextMonday(), TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_WEEKLY_RESET, ExistingPeriodicWorkPolicy.KEEP, request,
        )
    }

    private fun delayUntil(time: LocalTime): Long {
        val now = LocalDateTime.now()
        var target = LocalDateTime.of(now.toLocalDate(), time)
        if (target.isBefore(now)) target = target.plusDays(1)
        return Duration.between(now, target).toMillis()
    }

    private fun delayUntilNextMonday(): Long {
        val now = LocalDateTime.now()
        var target = LocalDateTime.of(now.toLocalDate(), LocalTime.MIDNIGHT)
        while (target.dayOfWeek != java.time.DayOfWeek.MONDAY || target.isBefore(now)) {
            target = target.plusDays(1)
        }
        return Duration.between(now, target).toMillis()
    }

    companion object {
        private const val WORK_DAILY_RESET = "work_daily_reset"
        private const val WORK_STREAK_REMINDER = "work_streak_reminder"
        private const val WORK_WEEKLY_RESET = "work_weekly_reset"
        private val DEFAULT_STREAK_REMINDER_TIME: LocalTime = LocalTime.of(20, 0) // 8 PM default
    }
}
