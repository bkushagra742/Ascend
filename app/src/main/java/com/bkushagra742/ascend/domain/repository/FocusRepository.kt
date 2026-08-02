package com.bkushagra742.ascend.domain.repository

import com.bkushagra742.ascend.domain.model.FocusSettings
import com.bkushagra742.ascend.domain.model.InstalledApp
import kotlinx.coroutines.flow.Flow

interface FocusRepository {
    fun observeSettings(): Flow<FocusSettings>
    suspend fun getSettings(): FocusSettings

    suspend fun setEnabled(enabled: Boolean)
    suspend fun toggleBlockedPackage(packageName: String, blocked: Boolean)

    /** Installed, launchable (non-system-only) apps for the app-picker screen. */
    suspend fun getInstalledApps(): List<InstalledApp>
}
