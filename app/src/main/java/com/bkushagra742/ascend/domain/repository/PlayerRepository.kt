package com.bkushagra742.ascend.domain.repository

import com.bkushagra742.ascend.domain.model.AttributeType
import com.bkushagra742.ascend.domain.model.PlayerProfile
import kotlinx.coroutines.flow.Flow

/**
 * Domain-layer contract. Presentation code (ViewModels) depends on THIS interface,
 * never on the Room-backed implementation — keeps the UI layer testable with a fake
 * without touching a real database.
 */
interface PlayerRepository {
    fun observeProfile(): Flow<PlayerProfile>
    suspend fun getProfile(): PlayerProfile

    /** Persists a fresh profile — called once, on first app launch. */
    suspend fun createProfileIfAbsent()

    /** Applies XP + currency + attribute gains atomically, recalculating level/rank. */
    suspend fun applyRewards(
        xpGained: Long,
        creditsGained: Long,
        essenceGained: Long,
        attributeGains: Map<AttributeType, Int>,
    )

    suspend fun spendCredits(amount: Long): Boolean
    suspend fun spendEssenceStones(amount: Long): Boolean
}
