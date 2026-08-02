package com.bkushagra742.ascend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bkushagra742.ascend.data.local.Converters

@Entity(tableName = "quests")
@TypeConverters(Converters::class)
data class QuestEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val typeName: String,          // QuestType enum .name
    val difficultyName: String,    // QuestDifficulty enum .name
    val baseXp: Long,
    val creditReward: Long,
    val attributeRewardsJson: String, // Map<AttributeType, Int> serialized
    val isCompletedToday: Boolean,
    val isCustom: Boolean,
    val lastCompletedEpochDay: Long?, // used by the daily/weekly reset workers
)
