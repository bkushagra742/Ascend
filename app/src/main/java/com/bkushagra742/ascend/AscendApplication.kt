package com.bkushagra742.ascend

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.bkushagra742.ascend.core.notification.NotificationHelper
import com.bkushagra742.ascend.core.work.AscendWorkScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application entry point. Annotated for Hilt so the DI graph is available app-wide.
 *
 * Also implements [Configuration.Provider] so WorkManager workers (quest reminders,
 * streak warnings, daily reward nudges — see FR-NOT-*) can use constructor injection
 * via [HiltWorkerFactory] instead of manual dependency wiring inside each Worker.
 */
@HiltAndroidApp
class AscendApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var workScheduler: AscendWorkScheduler

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        notificationHelper.ensureChannels()
        workScheduler.scheduleAll()
    }
}
