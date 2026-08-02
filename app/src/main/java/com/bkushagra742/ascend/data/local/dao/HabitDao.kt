package com.bkushagra742.ascend.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bkushagra742.ascend.data.local.entity.HabitChainEntity
import com.bkushagra742.ascend.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits ORDER BY title")
    fun observeAll(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): HabitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: HabitEntity)

    @Update
    suspend fun update(entity: HabitEntity)

    @Delete
    suspend fun delete(entity: HabitEntity)

    @Query("UPDATE habits SET isCompletedToday = 0")
    suspend fun clearCompletedTodayFlags()

    /** Any habit not completed yesterday has its streak broken at rollover. */
    @Query("UPDATE habits SET currentStreak = 0 WHERE lastCompletedEpochDay IS NULL OR lastCompletedEpochDay < :yesterdayEpochDay")
    suspend fun breakStreaksForIncompleteHabits(yesterdayEpochDay: Long)

    @Query("SELECT * FROM habit_chains")
    fun observeChains(): Flow<List<HabitChainEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChain(entity: HabitChainEntity)
}
