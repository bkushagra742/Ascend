package com.bkushagra742.ascend.presentation.dashboard

import com.bkushagra742.ascend.domain.model.PlayerProfile
import com.bkushagra742.ascend.domain.model.Quest

data class DashboardUiState(
    val isLoading: Boolean = true,
    val profile: PlayerProfile? = null,
    val todaysMissions: List<Quest> = emptyList(),
    val xpProgress: Float = 0f, // currentXp-within-level / xpToNextLevel, 0f..1f
    val currentStreak: Int = 0,
    val errorMessage: String? = null,
)
