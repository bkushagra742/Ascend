package com.bkushagra742.ascend.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bkushagra742.ascend.data.local.dao.HabitDao
import com.bkushagra742.ascend.data.local.dao.InventoryDao
import com.bkushagra742.ascend.data.local.dao.PlayerProfileDao
import com.bkushagra742.ascend.data.local.dao.QuestDao
import com.bkushagra742.ascend.data.local.dao.StreakDao
import com.bkushagra742.ascend.data.local.entity.HabitChainEntity
import com.bkushagra742.ascend.data.local.entity.HabitEntity
import com.bkushagra742.ascend.data.local.entity.InventoryItemEntity
import com.bkushagra742.ascend.data.local.entity.PlayerProfileEntity
import com.bkushagra742.ascend.data.local.entity.QuestEntity
import com.bkushagra742.ascend.data.local.entity.StreakStateEntity

/**
 * Version starts at 1 — this is still pre-release (no shipped users yet), so new entities
 * are being added directly rather than via Migration objects. THE MOMENT this build reaches
 * a real device/user, that stops: every future schema change ships a Migration (never
 * fallbackToDestructiveMigration in a shipped build) — losing a user's Level/streak/Vault
 * data is losing months of real-world progress, not just app state.
 */
@Database(
    entities = [
        PlayerProfileEntity::class,
        QuestEntity::class,
        HabitEntity::class,
        HabitChainEntity::class,
        StreakStateEntity::class,
        InventoryItemEntity::class,
    ],
    version = 1,
    exportSchema = true, // schema JSON checked into version control for migration testing
)
@TypeConverters(Converters::class)
abstract class AscendDatabase : RoomDatabase() {
    abstract fun playerProfileDao(): PlayerProfileDao
    abstract fun questDao(): QuestDao
    abstract fun habitDao(): HabitDao
    abstract fun streakDao(): StreakDao
    abstract fun inventoryDao(): InventoryDao

    companion object {
        const val DATABASE_NAME = "ascend.db"
    }
}
