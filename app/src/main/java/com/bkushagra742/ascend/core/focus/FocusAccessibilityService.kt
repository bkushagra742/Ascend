package com.bkushagra742.ascend.core.focus

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.bkushagra742.ascend.domain.usecase.ShouldBlockDistractingAppUseCase
import com.bkushagra742.ascend.presentation.focus.FocusBlockActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * FR-FOC-03: watches for foreground-app changes via TYPE_WINDOW_STATE_CHANGED events —
 * this is the ONLY signal this service reads. It does not read screen content, does not
 * use TYPE_VIEW_* events, and has no need for `canRetrieveWindowContent` — deliberately
 * the narrowest possible AccessibilityService capability set, both because that's all
 * the feature needs and because Play Store policy scrutiny is proportional to what a
 * service *can* do, not just what it currently does.
 *
 * When the foreground package matches a blocked app AND today's Missions aren't done,
 * it launches [FocusBlockActivity] as a full-screen redirect — no SYSTEM_ALERT_WINDOW
 * overlay permission needed, which keeps this feature's permission footprint small.
 */
@AndroidEntryPoint
class FocusAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var shouldBlockDistractingApp: ShouldBlockDistractingAppUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var lastCheckedPackage: String? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val packageName = event.packageName?.toString() ?: return

        // Debounce: a single app launch fires multiple window-state-changed events as
        // its own windows settle — only act on an actual foreground-app CHANGE, not
        // every event within the same app.
        if (packageName == lastCheckedPackage) return
        lastCheckedPackage = packageName

        // Never evaluate/block our own app's windows.
        if (packageName == applicationContext.packageName) return

        serviceScope.launch {
            if (shouldBlockDistractingApp(packageName)) {
                val intent = Intent(this@FocusAccessibilityService, FocusBlockActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra(FocusBlockActivity.EXTRA_BLOCKED_PACKAGE, packageName)
                }
                startActivity(intent)
            }
        }
    }

    override fun onInterrupt() {
        // Required override — no cleanup needed, this service holds no ongoing resources
        // that need to be torn down mid-operation.
    }
}
