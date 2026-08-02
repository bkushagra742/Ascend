package com.bkushagra742.ascend.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bkushagra742.ascend.data.local.entity.InventoryItemEntity
import com.bkushagra742.ascend.data.local.entity.StreakStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {

    @Query("SELECT * FROM streak_state WHERE id = 1 LIMIT 1")
    fun observeStreak(): Flow<StreakStateEntity?>

    @Query("SELECT * FROM streak_state WHERE id = 1 LIMIT 1")
    suspend fun getStreak(): StreakStateEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: StreakStateEntity)

    @Update
    suspend fun update(entity: StreakStateEntity)
}

@Dao
interface InventoryDao {

    @Query("SELECT * FROM inventory_items WHERE typeName = :typeName LIMIT 1")
    suspend fun getItem(typeName: String): InventoryItemEntity?

    @Query("SELECT * FROM inventory_items WHERE typeName = :typeName LIMIT 1")
    fun observeItem(typeName: String): Flow<InventoryItemEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: InventoryItemEntity)
}
