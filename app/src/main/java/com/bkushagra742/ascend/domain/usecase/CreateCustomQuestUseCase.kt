package com.bkushagra742.ascend.domain.usecase

import com.bkushagra742.ascend.domain.model.AttributeType
import com.bkushagra742.ascend.domain.model.Quest
import com.bkushagra742.ascend.domain.model.QuestDifficulty
import com.bkushagra742.ascend.domain.model.QuestType
import com.bkushagra742.ascend.domain.repository.QuestRepository
import java.util.UUID
import javax.inject.Inject

/**
 * Creates a custom Mission with a user-chosen difficulty. This is where "easy/mid/hard"
 * (the request) becomes real numbers: base XP/credits scale by QuestDifficulty's
 * multiplier, then get clamped to Quest.CUSTOM_QUEST_MAX_XP/CREDITS (FR-QST-04) so a
 * "Hard" custom quest can't be used to inflate rewards past what a designed Hard Mission
 * would give.
 */
class CreateCustomQuestUseCase @Inject constructor(
    private val questRepository: QuestRepository,
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        difficulty: QuestDifficulty,
        attributeRewards: Map<AttributeType, Int>,
    ): Result<Quest> = try {
        require(title.isNotBlank()) { "Quest title cannot be empty" }

        val baseXp = (BASE_XP_UNSCALED * difficulty.xpMultiplier).toLong()
        val baseCredits = (BASE_CREDITS_UNSCALED * difficulty.xpMultiplier).toLong()

        val quest = Quest(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            description = description.trim(),
            type = QuestType.CUSTOM,
            difficulty = difficulty,
            baseXp = baseXp,
            creditReward = baseCredits,
            attributeRewards = attributeRewards,
            isCompletedToday = false,
            isCustom = true,
        )

        // createCustomQuest() applies the hard XP/credit caps itself (defense in depth)
        Result.success(questRepository.createCustomQuest(quest))
    } catch (e: Exception) {
        Result.failure(e)
    }

    companion object {
        // Deliberately modest unscaled bases — Easy should feel like "a couple minutes
        // of effort", Boss should feel meaningful but still respects the hard cap above.
        private const val BASE_XP_UNSCALED = 40L
        private const val BASE_CREDITS_UNSCALED = 15L
    }
}
