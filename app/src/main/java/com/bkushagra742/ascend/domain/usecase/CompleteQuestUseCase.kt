package com.bkushagra742.ascend.domain.usecase

import com.bkushagra742.ascend.domain.model.Quest
import com.bkushagra742.ascend.domain.repository.PlayerRepository
import com.bkushagra742.ascend.domain.repository.QuestRepository
import com.bkushagra742.ascend.domain.repository.StreakRepository
import javax.inject.Inject

/**
 * Orchestrates: mark quest complete -> compute XP via CalculateXpUseCase -> apply
 * rewards to the player profile -> record the streak-qualifying action. Kept as its
 * own use case (rather than folded into the repository) because it coordinates THREE
 * repositories — a textbook use-case responsibility in Clean Architecture, and it's
 * exactly the kind of logic that needs a unit test independent of Room/Android.
 */
class CompleteQuestUseCase @Inject constructor(
    private val questRepository: QuestRepository,
    private val playerRepository: PlayerRepository,
    private val streakRepository: StreakRepository,
    private val calculateXp: CalculateXpUseCase,
) {
    suspend operator fun invoke(questId: String, currentStreakDays: Int, comboCount: Int): Result<Quest> {
        return try {
            val quest = questRepository.completeQuest(questId)

            val xpGained = calculateXp(
                CalculateXpUseCase.Params(
                    baseXp = quest.baseXp,
                    difficulty = quest.difficulty,
                    streakDays = currentStreakDays,
                    comboCount = comboCount,
                    isBossMission = quest.type == com.bkushagra742.ascend.domain.model.QuestType.ELITE_MISSION,
                )
            )

            playerRepository.applyRewards(
                xpGained = xpGained,
                creditsGained = quest.creditReward,
                essenceGained = 0L, // ENS is a rare/earned currency, not awarded per-quest in V1
                attributeGains = quest.attributeRewards,
            )

            // FR-STR-01: Missions are the default streak-qualifying action. Idempotent —
            // completing a 2nd/3rd Mission today doesn't double-advance the streak.
            streakRepository.recordQualifyingAction()

            Result.success(quest)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

