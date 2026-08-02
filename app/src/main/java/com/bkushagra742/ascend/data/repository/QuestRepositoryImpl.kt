package com.bkushagra742.ascend.data.repository

import com.bkushagra742.ascend.data.local.dao.QuestDao
import com.bkushagra742.ascend.data.local.entity.QuestEntity
import com.bkushagra742.ascend.domain.model.AttributeType
import com.bkushagra742.ascend.domain.model.Quest
import com.bkushagra742.ascend.domain.model.QuestDifficulty
import com.bkushagra742.ascend.domain.model.QuestType
import com.bkushagra742.ascend.domain.repository.QuestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestRepositoryImpl @Inject constructor(
    private val dao: QuestDao,
) : QuestRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override fun observeQuests(type: QuestType?): Flow<List<Quest>> =
        if (type == null) dao.observeAll().map { list -> list.map { it.toDomain() } }
        else dao.observeByType(type.name).map { list -> list.map { it.toDomain() } }

    override suspend fun getQuest(id: String): Quest? = dao.getById(id)?.toDomain()

    override suspend fun completeQuest(id: String): Quest {
        val entity = dao.getById(id) ?: error("Quest $id not found")
        val updated = entity.copy(isCompletedToday = true, lastCompletedEpochDay = todayEpochDay())
        dao.update(updated)
        return updated.toDomain()
    }

    override suspend fun undoCompleteQuest(id: String): Quest {
        val entity = dao.getById(id) ?: error("Quest $id not found")
        val updated = entity.copy(isCompletedToday = false)
        dao.update(updated)
        return updated.toDomain()
    }

    override suspend fun createCustomQuest(quest: Quest): Quest {
        // FR-QST-04: hard cap enforced here too (defense in depth — UI should already
        // clamp input, but the repository is the last line of defense against bad data).
        val clampedXp = quest.baseXp.coerceAtMost(Quest.CUSTOM_QUEST_MAX_XP)
        val clampedCredits = quest.creditReward.coerceAtMost(Quest.CUSTOM_QUEST_MAX_CREDITS)
        val clamped = quest.copy(baseXp = clampedXp, creditReward = clampedCredits, isCustom = true)
        dao.upsert(clamped.toEntity())
        return clamped
    }

    override suspend fun resetDailyQuests() = dao.resetDailyMissions()
    override suspend fun resetWeeklyQuests() = dao.resetWeeklyOperations()

    private fun todayEpochDay(): Long = java.time.LocalDate.now().toEpochDay()

    private fun QuestEntity.toDomain(): Quest = Quest(
        id = id,
        title = title,
        description = description,
        type = QuestType.valueOf(typeName),
        difficulty = QuestDifficulty.valueOf(difficultyName),
        baseXp = baseXp,
        creditReward = creditReward,
        attributeRewards = if (attributeRewardsJson.isBlank()) emptyMap()
        else json.decodeFromString<Map<String, Int>>(attributeRewardsJson)
            .mapKeys { AttributeType.valueOf(it.key) },
        isCompletedToday = isCompletedToday,
        isCustom = isCustom,
    )

    private fun Quest.toEntity(): QuestEntity = QuestEntity(
        id = id,
        title = title,
        description = description,
        typeName = type.name,
        difficultyName = difficulty.name,
        baseXp = baseXp,
        creditReward = creditReward,
        attributeRewardsJson = json.encodeToString(attributeRewards.mapKeys { it.key.name }),
        isCompletedToday = isCompletedToday,
        isCustom = isCustom,
        lastCompletedEpochDay = null,
    )
}
