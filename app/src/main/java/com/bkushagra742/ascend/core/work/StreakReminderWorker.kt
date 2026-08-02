package com.bkushagra742.ascend.core.work

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bkushagra742.ascend.core.notification.NotificationHelper
import com.bkushagra742.ascend.domain.repository.StreakRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * FR-STR-03 / FR-NOT-*: runs once daily at the user's configured reminder time (default
 * scheduled by [AscendWorkScheduler]). Only shows a notification if today's streak is
 * NOT already qualified — someone who already completed a Mission this morning shouldn't
 * get a nagging "don't lose your streak" notification tonight.
 */
@HiltWorker
class StreakReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val streakRepository: StreakRepository,
    private val notificationHelper: NotificationHelper,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (!hasNotificationPermission()) return Result.success() // nothing we can do, don't retry-loop

        val streak = streakRepository.getStreak()
        if (!streak.todayQualified) {
            notificationHelper.showStreakWarning(streak.currentStreak)
        }
        return Result.success()
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
}
