package com.bkushagra742.ascend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streak_state")
data class StreakStateEntity(
    @PrimaryKey val id: Long = 1L, // single-row, same pattern as PlayerProfileEntity
    val currentStreak: Int,
    val longestStreak: Int,
    val lastQualifyingActionEpochDay: Long?,
    val todayQualified: Boolean,
)

/** Minimal inventory table — just enough for the Streak Saver (FR-STR-02). See kdoc on
 * domain.model.InventoryItemType for why this isn't the full Vault system yet. */
@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey val typeName: String, // InventoryItemType enum .name
    val quantity: Int,
)
