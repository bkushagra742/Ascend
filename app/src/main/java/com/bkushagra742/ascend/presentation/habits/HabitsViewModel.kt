package com.bkushagra742.ascend.presentation.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkushagra742.ascend.domain.model.Habit
import com.bkushagra742.ascend.domain.model.StreakState
import com.bkushagra742.ascend.domain.repository.HabitRepository
import com.bkushagra742.ascend.domain.repository.StreakRepository
import com.bkushagra742.ascend.domain.usecase.CompleteHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HabitsUiState(
    val isLoading: Boolean = true,
    val habits: List<Habit> = emptyList(),
    val streak: StreakState = StreakState.initial(),
    val errorMessage: String? = null,
)

@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val streakRepository: StreakRepository,
    private val completeHabitUseCase: CompleteHabitUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitsUiState())
    val uiState: StateFlow<HabitsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                habitRepository.observeHabits(),
                streakRepository.observeStreak(),
            ) { habits, streak ->
                HabitsUiState(isLoading = false, habits = habits, streak = streak)
            }.collect { _uiState.value = it }
        }
    }

    fun onToggleHabit(habit: Habit) {
        viewModelScope.launch {
            if (habit.isCompletedToday) {
                habitRepository.undoCompleteHabit(habit.id)
            } else {
                completeHabitUseCase(habit.id)
                    .onFailure { e -> _uiState.value = _uiState.value.copy(errorMessage = e.message) }
            }
        }
    }
}
