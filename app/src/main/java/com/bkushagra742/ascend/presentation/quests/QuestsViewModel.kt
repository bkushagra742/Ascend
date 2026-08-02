package com.bkushagra742.ascend.presentation.quests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkushagra742.ascend.domain.model.Quest
import com.bkushagra742.ascend.domain.model.QuestType
import com.bkushagra742.ascend.domain.repository.QuestRepository
import com.bkushagra742.ascend.domain.usecase.CompleteQuestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuestsUiState(
    val isLoading: Boolean = true,
    val missions: List<Quest> = emptyList(),
    val errorMessage: String? = null,
)

@HiltViewModel
class QuestsViewModel @Inject constructor(
    private val questRepository: QuestRepository,
    private val completeQuestUseCase: CompleteQuestUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuestsUiState())
    val uiState: StateFlow<QuestsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            questRepository.observeQuests(QuestType.MISSION).collect { missions ->
                _uiState.value = QuestsUiState(isLoading = false, missions = missions)
            }
        }
    }

    fun onCompleteQuest(questId: String) {
        viewModelScope.launch {
            completeQuestUseCase(questId, currentStreakDays = 0, comboCount = 0)
                .onFailure { e -> _uiState.value = _uiState.value.copy(errorMessage = e.message) }
        }
    }
}
