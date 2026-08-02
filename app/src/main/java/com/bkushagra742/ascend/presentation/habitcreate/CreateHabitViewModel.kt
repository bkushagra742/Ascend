package com.bkushagra742.ascend.presentation.habitcreate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkushagra742.ascend.domain.model.Habit
import com.bkushagra742.ascend.domain.model.HabitRecurrence
import com.bkushagra742.ascend.domain.model.HabitTemplate
import com.bkushagra742.ascend.domain.model.HabitType
import com.bkushagra742.ascend.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CreateHabitUiState(
    val didSave: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class CreateHabitViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateHabitUiState())
    val uiState: StateFlow<CreateHabitUiState> = _uiState.asStateFlow()

    fun addFromTemplate(template: HabitTemplate) {
        viewModelScope.launch {
            habitRepository.createHabit(
                Habit(
                    id = UUID.randomUUID().toString(),
                    title = template.title,
                    description = template.description,
                    type = template.type,
                    recurrence = HabitRecurrence.DAILY,
                    attributeRewards = template.attributeRewards,
                    isCompletedToday = false,
                    currentStreak = 0,
                    longestStreak = 0,
                    masteryXp = 0L,
                    chainId = null,
                )
            )
            _uiState.value = _uiState.value.copy(didSave = true)
        }
    }

    fun addCustom(title: String, description: String, type: HabitType) {
        if (title.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Give your habit a title first.")
            return
        }
        viewModelScope.launch {
            habitRepository.createHabit(
                Habit(
                    id = UUID.randomUUID().toString(),
                    title = title.trim(),
                    description = description.trim(),
                    type = type,
                    recurrence = HabitRecurrence.DAILY,
                    attributeRewards = mapOf(com.bkushagra742.ascend.domain.model.AttributeType.DISCIPLINE to 1),
                    isCompletedToday = false,
                    currentStreak = 0,
                    longestStreak = 0,
                    masteryXp = 0L,
                    chainId = null,
                )
            )
            _uiState.value = _uiState.value.copy(didSave = true)
        }
    }
}
