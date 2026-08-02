package com.bkushagra742.ascend.domain.repository

import com.bkushagra742.ascend.domain.model.Quest
import com.bkushagra742.ascend.domain.model.QuestType
import kotlinx.coroutines.flow.Flow

interface QuestRepository {
    fun observeQuests(type: QuestType? = null): Flow<List<Quest>>
    suspend fun getQuest(id: String): Quest?

    /** FR-QST-05: completing is undoable same-day, so this returns the previous state
     * needed to reverse the action rather than just flipping a boolean blind. */
    suspend fun completeQuest(id: String): Quest
    suspend fun undoCompleteQuest(id: String): Quest

    suspend fun createCustomQuest(quest: Quest): Quest

    /** Called by a WorkManager job at each user's configured reset hour (FR-QST-01/02). */
    suspend fun resetDailyQuests()
    suspend fun resetWeeklyQuests()
}
