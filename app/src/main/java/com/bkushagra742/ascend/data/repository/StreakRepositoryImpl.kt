package com.bkushagra742.ascend.data.repository

import com.bkushagra742.ascend.data.local.dao.InventoryDao
import com.bkushagra742.ascend.data.local.dao.StreakDao
import com.bkushagra742.ascend.data.local.entity.InventoryItemEntity
import com.bkushagra742.ascend.data.local.entity.StreakStateEntity
import com.bkushagra742.ascend.domain.model.InventoryItemType
import com.bkushagra742.ascend.domain.model.StreakState
import com.bkushagra742.ascend.domain.repository.StreakRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreakRepositoryImpl @Inject constructor(
    private val streakDao: StreakDao,
    private val inventoryDao: InventoryDao,
) : StreakRepository {

    override fun observeStreak(): Flow<StreakState> =
        combine(
            streakDao.observeStreak().filterNotNull(),
            inventoryDao.observeItem(InventoryItemType.STREAK_SAVER.name),
        ) { streak, freezeItem ->
            streak.toDomain(freezeTokens = freezeItem?.quantity ?: 0)
        }

    override suspend fun getStreak(): StreakState {
        val existing = streakDao.getStreak()
        val freezeCount = getFreezeTokenCount()
        if (existing != null) return existing.toDomain(freezeCount)
        val fresh = StreakStateEntity(
            currentStreak = 0, longestStreak = 0,
            lastQualifyingActionEpochDay = null, todayQualified = false,
        )
        streakDao.insert(fresh)
        return fresh.toDomain(freezeCount)
    }

    override suspend fun recordQualifyingAction() {
        val today = LocalDate.now().toEpochDay()
        val current = streakDao.getStreak() ?: StreakStateEntity(
            currentStreak = 0, longestStreak = 0, lastQualifyingActionEpochDay = null, todayQualified = false,
        ).also { streakDao.insert(it) }

        // Idempotent: already qualified today, nothing to do.
        if (current.lastQualifyingActionEpochDay == today) return

        val wasYesterday = current.lastQualifyingActionEpochDay == today - 1
        val newStreak = if (wasYesterday || current.lastQualifyingActionEpochDay == null) {
            current.currentStreak + 1
        } else {
            // A gap existed but wasn't caught by the rollover worker (e.g. app was
            // closed for days) — start fresh rather than silently inflating the streak.
            1
        }

        streakDao.update(
            current.copy(
                currentStreak = newStreak,
                longestStreak = maxOf(current.longestStreak, newStreak),
                lastQualifyingActionEpochDay = today,
                todayQualified = true,
            )
        )
    }

    override suspend fun evaluateDailyRollover(): Boolean {
        val today = LocalDate.now().toEpochDay()
        val current = streakDao.getStreak() ?: return true // nothing to evaluate yet

        val qualifiedYesterday = current.lastQualifyingActionEpochDay == today - 1
        if (qualifiedYesterday) {
            streakDao.update(current.copy(todayQualified = false))
            return true
        }

        // Missed yesterday — try to spend a Streak Saver (FR-STR-02) before breaking the streak.
        val freezeItem = inventoryDao.getItem(InventoryItemType.STREAK_SAVER.name)
        return if (freezeItem != null && freezeItem.quantity > 0) {
            inventoryDao.upsert(freezeItem.copy(quantity = freezeItem.quantity - 1))
            streakDao.update(
                current.copy(
                    // Streak preserved — but we still advance lastQualifyingActionEpochDay
                    // to "yesterday" so today isn't immediately flagged as another gap.
                    lastQualifyingActionEpochDay = today - 1,
                    todayQualified = false,
                )
            )
            true
        } else {
            streakDao.update(
                current.copy(currentStreak = 0, todayQualified = false)
            )
            false
        }
    }

    override suspend fun addFreezeTokens(count: Int) {
        val existing = inventoryDao.getItem(InventoryItemType.STREAK_SAVER.name)
        val newQuantity = (existing?.quantity ?: 0) + count
        inventoryDao.upsert(InventoryItemEntity(InventoryItemType.STREAK_SAVER.name, newQuantity))
    }

    override suspend fun getFreezeTokenCount(): Int =
        inventoryDao.getItem(InventoryItemType.STREAK_SAVER.name)?.quantity ?: 0

    private fun StreakStateEntity.toDomain(freezeTokens: Int): StreakState = StreakState(
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        lastQualifyingActionEpochDay = lastQualifyingActionEpochDay,
        freezeTokensAvailable = freezeTokens,
        todayQualified = todayQualified,
    )
}
