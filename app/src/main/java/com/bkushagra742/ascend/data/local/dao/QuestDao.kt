package com.bkushagra742.ascend.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bkushagra742.ascend.data.local.entity.QuestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {

    @Query("SELECT * FROM quests ORDER BY typeName, title")
    fun observeAll(): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests WHERE typeName = :typeName ORDER BY title")
    fun observeByType(typeName: String): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): QuestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: QuestEntity)

    @Update
    suspend fun update(entity: QuestEntity)

    @Query("UPDATE quests SET isCompletedToday = 0 WHERE typeName = 'MISSION'")
    suspend fun resetDailyMissions()

    @Query("UPDATE quests SET isCompletedToday = 0 WHERE typeName = 'OPERATION'")
    suspend fun resetWeeklyOperations()
}
