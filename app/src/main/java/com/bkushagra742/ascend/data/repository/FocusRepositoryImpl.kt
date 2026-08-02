package com.bkushagra742.ascend.data.repository

import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bkushagra742.ascend.domain.model.FocusSettings
import com.bkushagra742.ascend.domain.model.InstalledApp
import com.bkushagra742.ascend.domain.repository.FocusRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.focusDataStore by preferencesDataStore(name = "focus_settings")

@Singleton
class FocusRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : FocusRepository {

    private val keyEnabled = booleanPreferencesKey("focus_enabled")
    private val keyBlockedPackages = stringSetPreferencesKey("focus_blocked_packages")

    override fun observeSettings(): Flow<FocusSettings> =
        context.focusDataStore.data.map { prefs ->
            FocusSettings(
                isEnabled = prefs[keyEnabled] ?: false,
                blockedPackages = prefs[keyBlockedPackages] ?: emptySet(),
            )
        }

    override suspend fun getSettings(): FocusSettings = observeSettings().first()

    override suspend fun setEnabled(enabled: Boolean) {
        context.focusDataStore.edit { it[keyEnabled] = enabled }
    }

    override suspend fun toggleBlockedPackage(packageName: String, blocked: Boolean) {
        context.focusDataStore.edit { prefs ->
            val current = prefs[keyBlockedPackages] ?: emptySet()
            prefs[keyBlockedPackages] = if (blocked) current + packageName else current - packageName
        }
    }

    override suspend fun getInstalledApps(): List<InstalledApp> {
        val pm = context.packageManager
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)

        // Requires the <queries> declaration in the manifest (Android 11+ package
        // visibility) — without it this silently returns an empty/partial list rather
        // than crashing, which is why it's worth flagging in code, not just the manifest.
        val resolved = pm.queryIntentActivities(launcherIntent, 0)

        return resolved
            .filter { it.activityInfo.packageName != context.packageName } // never let Ascend block itself
            .map { info ->
                InstalledApp(
                    packageName = info.activityInfo.packageName,
                    label = info.loadLabel(pm).toString(),
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    }
}
