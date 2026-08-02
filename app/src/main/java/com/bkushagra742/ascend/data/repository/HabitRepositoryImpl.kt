package com.bkushagra742.ascend.data.repository

import com.bkushagra742.ascend.data.local.dao.HabitDao
import com.bkushagra742.ascend.data.local.entity.HabitChainEntity
import com.bkushagra742.ascend.data.local.entity.HabitEntity
import com.bkushagra742.ascend.domain.model.AttributeType
import com.bkushagra742.ascend.domain.model.Habit
import com.bkushagra742.ascend.domain.model.HabitChain
import com.bkushagra742.ascend.domain.model.HabitMasteryCurve
import com.bkushagra742.ascend.domain.model.HabitRecurrence
import com.bkushagra742.ascend.domain.model.HabitType
import com.bkushagra742.ascend.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val dao: HabitDao,
) : HabitRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override fun observeHabits(): Flow<List<Habit>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getHabit(id: String): Habit? = dao.getById(id)?.toDomain()

    override suspend fun completeHabit(id: String): Habit {
        val entity = dao.getById(id) ?: error("Habit $id not found")
        val today = LocalDate.now().toEpochDay()
        val wasYesterday = entity.lastCompletedEpochDay == today - 1
        val newStreak = if (wasYesterday || entity.lastCompletedEpochDay == null) entity.currentStreak + 1 else 1

        val updated = entity.copy(
            isCompletedToday = true,
            lastCompletedEpochDay = today,
            currentStreak = newStreak,
            longestStreak = maxOf(entity.longestStreak, newStreak),
            masteryXp = entity.masteryXp + HabitMasteryCurve.MASTERY_XP_PER_COMPLETION,
        )
        dao.update(updated)
        return updated.toDomain()
    }

    override suspend fun undoCompleteHabit(id: String): Habit {
        val entity = dao.getById(id) ?: error("Habit $id not found")
        val updated = entity.copy(
            isCompletedToday = false,
            currentStreak = (entity.currentStreak - 1).coerceAtLeast(0),
            masteryXp = (entity.masteryXp - HabitMasteryCurve.MASTERY_XP_PER_COMPLETION).coerceAtLeast(0),
        )
        dao.update(updated)
        return updated.toDomain()
    }

    override suspend fun createHabit(habit: Habit): Habit {
        dao.upsert(habit.toEntity())
        return habit
    }

    override suspend fun deleteHabit(id: String) {
        dao.getById(id)?.let { dao.delete(it) }
    }

    override fun observeChains(): Flow<List<HabitChain>> =
        dao.observeChains().map { list -> list.map { it.toDomain() } }

    override suspend fun createChain(chain: HabitChain): HabitChain {
        dao.upsertChain(chain.toEntity())
        return chain
    }

    override suspend fun resetDailyHabits() {
        val yesterday = LocalDate.now().minusDays(1).toEpochDay()
        dao.breakStreaksForIncompleteHabits(yesterday)
        dao.clearCompletedTodayFlags()
    }

    private fun HabitEntity.toDomain(): Habit = Habit(
        id = id,
        title = title,
        description = description,
        type = HabitType.valueOf(typeName),
        recurrence = HabitRecurrence.valueOf(recurrenceName),
        attributeRewards = if (attributeRewardsJson.isBlank()) emptyMap()
        else json.decodeFromString<Map<String, Int>>(attributeRewardsJson)
            .mapKeys { AttributeType.valueOf(it.key) },
        isCompletedToday = isCompletedToday,
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        masteryXp = masteryXp,
        chainId = chainId,
    )

    private fun Habit.toEntity(): HabitEntity = HabitEntity(
        id = id,
        title = title,
        description = description,
        typeName = type.name,
        recurrenceName = recurrence.name,
        attributeRewardsJson = json.encodeToString(attributeRewards.mapKeys { it.key.name }),
        isCompletedToday = isCompletedToday,
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        masteryXp = masteryXp,
        chainId = chainId,
        lastCompletedEpochDay = null,
    )

    private fun HabitChainEntity.toDomain(): HabitChain = HabitChain(
        id = id,
        name = name,
        habitIds = habitIdsCsv.split(",").filter { it.isNotBlank() },
        bonusCredits = bonusCredits,
        bonusXp = bonusXp,
    )

    private fun HabitChain.toEntity(): HabitChainEntity = HabitChainEntity(
        id = id,
        name = name,
        habitIdsCsv = habitIds.joinToString(","),
        bonusCredits = bonusCredits,
        bonusXp = bonusXp,
    )
}
