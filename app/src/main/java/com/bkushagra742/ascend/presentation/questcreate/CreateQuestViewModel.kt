package com.bkushagra742.ascend.presentation.questcreate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkushagra742.ascend.domain.model.AttributeType
import com.bkushagra742.ascend.domain.model.QuestDifficulty
import com.bkushagra742.ascend.domain.usecase.CreateCustomQuestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateQuestUiState(
    val title: String = "",
    val description: String = "",
    val difficulty: QuestDifficulty = QuestDifficulty.EASY,
    val selectedAttribute: AttributeType = AttributeType.DISCIPLINE,
    val isSaving: Boolean = false,
    val didSave: Boolean = false,
    val errorMessage: String? = null,
) {
    /** Preview numbers shown live as the user changes difficulty — mirrors
     * CreateCustomQuestUseCase's own math so what you see is what you get. */
    val previewXp: Long get() = (40L * difficulty.xpMultiplier).toLong().coerceAtMost(
        com.bkushagra742.ascend.domain.model.Quest.CUSTOM_QUEST_MAX_XP
    )
    val previewCredits: Long get() = (15L * difficulty.xpMultiplier).toLong().coerceAtMost(
        com.bkushagra742.ascend.domain.model.Quest.CUSTOM_QUEST_MAX_CREDITS
    )
}

@HiltViewModel
class CreateQuestViewModel @Inject constructor(
    private val createCustomQuest: CreateCustomQuestUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateQuestUiState())
    val uiState: StateFlow<CreateQuestUiState> = _uiState.asStateFlow()

    fun onTitleChange(value: String) {
        _uiState.value = _uiState.value.copy(title = value)
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun onDifficultySelect(difficulty: QuestDifficulty) {
        _uiState.value = _uiState.value.copy(difficulty = difficulty)
    }

    fun onAttributeSelect(attribute: AttributeType) {
        _uiState.value = _uiState.value.copy(selectedAttribute = attribute)
    }

    fun onSave() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Give your quest a title first.")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, errorMessage = null)
            createCustomQuest(
                title = state.title,
                description = state.description,
                difficulty = state.difficulty,
                attributeRewards = mapOf(state.selectedAttribute to attributeGainFor(state.difficulty)),
            ).onSuccess {
                _uiState.value = _uiState.value.copy(isSaving = false, didSave = true)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = e.message)
            }
        }
    }

    private fun attributeGainFor(difficulty: QuestDifficulty): Int = when (difficulty) {
        QuestDifficulty.EASY -> 1
        QuestDifficulty.MEDIUM -> 2
        QuestDifficulty.HARD -> 4
        QuestDifficulty.BOSS -> 7
    }
}
