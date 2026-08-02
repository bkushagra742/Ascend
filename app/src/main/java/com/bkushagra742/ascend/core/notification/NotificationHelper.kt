package com.bkushagra742.ascend.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bkushagra742.ascend.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralizes notification channel setup + building. FR-NOT-01 (per-category toggles)
 * is a Settings-screen concern layered on top of this — this class always builds the
 * notification if asked; the caller (a Worker, checking DataStore prefs) decides whether
 * to ask at all. Keeping the "should we notify" decision OUT of this class means it stays
 * simple and testable.
 */
@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun ensureChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_STREAK,
                "Streak Reminders",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply { description = "Reminds you before your daily streak expires" }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_QUEST,
                "Quest & Habit Reminders",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply { description = "Mission, habit, study, and workout reminders" }
        )
    }

    /** FR-STR-03: fires only if today's streak-qualifying action isn't done yet — the
     * caller (StreakReminderWorker) is responsible for that check before calling this. */
    fun showStreakWarning(currentStreak: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_STREAK)
            .setSmallIcon(R.drawable.ic_notification_bell)
            .setContentTitle("Don't lose your streak!")
            .setContentText(
                if (currentStreak > 0) "Your $currentStreak-day streak is waiting — complete a Mission today."
                else "Complete a Mission today to start your streak."
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        // Caller must have already confirmed POST_NOTIFICATIONS is granted (Android 13+) —
        // NotificationManagerCompat.areNotificationsEnabled() check belongs in the Worker,
        // not duplicated here.
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_STREAK_WARNING, notification)
    }

    companion object {
        const val CHANNEL_STREAK = "channel_streak"
        const val CHANNEL_QUEST = "channel_quest"
        const val NOTIFICATION_ID_STREAK_WARNING = 1001
    }
}
