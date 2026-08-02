package com.bkushagra742.ascend.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkushagra742.ascend.domain.model.QuestType
import com.bkushagra742.ascend.domain.model.XpCurve
import com.bkushagra742.ascend.domain.repository.PlayerRepository
import com.bkushagra742.ascend.domain.repository.QuestRepository
import com.bkushagra742.ascend.domain.repository.StreakRepository
import com.bkushagra742.ascend.domain.usecase.CompleteQuestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val questRepository: QuestRepository,
    private val streakRepository: StreakRepository,
    private val completeQuestUseCase: CompleteQuestUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            playerRepository.createProfileIfAbsent()
        }
        observeState()
    }

    private fun observeState() {
        viewModelScope.launch {
            combine(
                playerRepository.observeProfile(),
                questRepository.observeQuests(QuestType.MISSION),
                streakRepository.observeStreak(),
            ) { profile, missions, streak ->
                val xpIntoLevel = profile.currentXp - XpCurve.xpRequiredForLevel(profile.level)
                val xpNeeded = XpCurve.xpForNextLevel(profile.level).coerceAtLeast(1)
                DashboardUiState(
                    isLoading = false,
                    profile = profile,
                    todaysMissions = missions,
                    xpProgress = (xpIntoLevel.toFloat() / xpNeeded.toFloat()).coerceIn(0f, 1f),
                    currentStreak = streak.currentStreak,
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onCompleteQuest(questId: String) {
        viewModelScope.launch {
            // TODO: comboCount is hardcoded until same-session combo tracking ships —
            // streak is now real (via StreakRepository), combo is the remaining TODO.
            completeQuestUseCase(questId, currentStreakDays = _uiState.value.currentStreak, comboCount = 0)
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(errorMessage = e.message)
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
