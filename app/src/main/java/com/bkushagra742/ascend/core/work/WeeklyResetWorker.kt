package com.bkushagra742.ascend.core.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bkushagra742.ascend.domain.repository.QuestRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/** FR-QST-02: resets Operations (weekly quests) at the user's configured week-start. */
@HiltWorker
class WeeklyResetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val questRepository: QuestRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = try {
        questRepository.resetWeeklyQuests()
        Result.success()
    } catch (e: Exception) {
        Result.retry()
    }
}
