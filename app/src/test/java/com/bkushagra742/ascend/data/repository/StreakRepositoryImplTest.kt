package com.bkushagra742.ascend.data.repository

import com.bkushagra742.ascend.data.local.dao.InventoryDao
import com.bkushagra742.ascend.data.local.dao.StreakDao
import com.bkushagra742.ascend.data.local.entity.InventoryItemEntity
import com.bkushagra742.ascend.data.local.entity.StreakStateEntity
import com.bkushagra742.ascend.domain.model.InventoryItemType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

/**
 * Fakes over real Room DAOs — this tests the Streak Saver / rollover BUSINESS LOGIC in
 * StreakRepositoryImpl without needing an actual database, per the "domain/data logic
 * should be unit-testable without Room/Android" principle established from day one.
 */
class StreakRepositoryImplTest {

    private lateinit var streakDao: FakeStreakDao
    private lateinit var inventoryDao: FakeInventoryDao
    private lateinit var repository: StreakRepositoryImpl

    @Before
    fun setup() {
        streakDao = FakeStreakDao()
        inventoryDao = FakeInventoryDao()
        repository = StreakRepositoryImpl(streakDao, inventoryDao)
    }

    @Test
    fun `rollover with no missed day keeps the streak intact`() = runTest {
        val today = LocalDate.now().toEpochDay()
        streakDao.state = StreakStateEntity(
            currentStreak = 5, longestStreak = 5,
            lastQualifyingActionEpochDay = today - 1, todayQualified = false,
        )

        val survived = repository.evaluateDailyRollover()

        assertTrue(survived)
        assertEquals(5, streakDao.state?.currentStreak)
    }

    @Test
    fun `missed day with a streak saver available preserves the streak and consumes one token`() = runTest {
        val today = LocalDate.now().toEpochDay()
        streakDao.state = StreakStateEntity(
            currentStreak = 7, longestStreak = 7,
            lastQualifyingActionEpochDay = today - 2, todayQualified = false, // missed yesterday
        )
        inventoryDao.items[InventoryItemType.STREAK_SAVER.name] =
            InventoryItemEntity(InventoryItemType.STREAK_SAVER.name, quantity = 2)

        val survived = repository.evaluateDailyRollover()

        assertTrue(survived)
        assertEquals(7, streakDao.state?.currentStreak) // preserved, not reset
        assertEquals(1, inventoryDao.items[InventoryItemType.STREAK_SAVER.name]?.quantity) // one consumed
    }

    @Test
    fun `missed day with no streak saver breaks the streak`() = runTest {
        val today = LocalDate.now().toEpochDay()
        streakDao.state = StreakStateEntity(
            currentStreak = 12, longestStreak = 12,
            lastQualifyingActionEpochDay = today - 2, todayQualified = false,
        )
        // no inventory entry at all — zero tokens

        val survived = repository.evaluateDailyRollover()

        assertFalse(survived)
        assertEquals(0, streakDao.state?.currentStreak)
        assertEquals(12, streakDao.state?.longestStreak) // longest streak record is untouched
    }

    @Test
    fun `recording a qualifying action twice in the same day only advances the streak once`() = runTest {
        repository.recordQualifyingAction()
        val afterFirst = streakDao.state?.currentStreak
        repository.recordQualifyingAction()
        val afterSecond = streakDao.state?.currentStreak

        assertEquals(afterFirst, afterSecond)
        assertEquals(1, afterSecond)
    }

    /** In-memory fake — mirrors the real DAO contract closely enough for repository-level tests. */
    private class FakeStreakDao : StreakDao {
        var state: StreakStateEntity? = null
        private val flow = MutableStateFlow<StreakStateEntity?>(null)

        override fun observeStreak(): Flow<StreakStateEntity?> = flow
        override suspend fun getStreak(): StreakStateEntity? = state
        override suspend fun insert(entity: StreakStateEntity) {
            if (state == null) { state = entity; flow.value = entity }
        }
        override suspend fun update(entity: StreakStateEntity) {
            state = entity
            flow.value = entity
        }
    }

    private class FakeInventoryDao : InventoryDao {
        val items = mutableMapOf<String, InventoryItemEntity>()
        private val flows = mutableMapOf<String, MutableStateFlow<InventoryItemEntity?>>()

        override suspend fun getItem(typeName: String): InventoryItemEntity? = items[typeName]
        override fun observeItem(typeName: String): Flow<InventoryItemEntity?> =
            flows.getOrPut(typeName) { MutableStateFlow(items[typeName]) }
        override suspend fun upsert(entity: InventoryItemEntity) {
            items[entity.typeName] = entity
            flows.getOrPut(entity.typeName) { MutableStateFlow(null) }.value = entity
        }
    }
}
