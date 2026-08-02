package com.bkushagra742.ascend.domain.repository

import com.bkushagra742.ascend.domain.model.Habit
import com.bkushagra742.ascend.domain.model.HabitChain
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun observeHabits(): Flow<List<Habit>>
    suspend fun getHabit(id: String): Habit?

    suspend fun completeHabit(id: String): Habit
    suspend fun undoCompleteHabit(id: String): Habit

    suspend fun createHabit(habit: Habit): Habit
    suspend fun deleteHabit(id: String)

    fun observeChains(): Flow<List<HabitChain>>
    suspend fun createChain(chain: HabitChain): HabitChain

    /** Called by the daily rollover worker — resets isCompletedToday and, for any habit
     * NOT completed the prior day, resets currentStreak to 0 (FR-HAB-01). */
    suspend fun resetDailyHabits()
}
