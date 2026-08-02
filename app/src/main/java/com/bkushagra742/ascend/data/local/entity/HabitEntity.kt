package com.bkushagra742.ascend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bkushagra742.ascend.data.local.Converters

@Entity(tableName = "habits")
@TypeConverters(Converters::class)
data class HabitEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val typeName: String,        // HabitType enum .name
    val recurrenceName: String,  // HabitRecurrence enum .name
    val attributeRewardsJson: String,
    val isCompletedToday: Boolean,
    val currentStreak: Int,
    val longestStreak: Int,
    val masteryXp: Long,
    val chainId: String?,
    val lastCompletedEpochDay: Long?,
)

@Entity(tableName = "habit_chains")
data class HabitChainEntity(
    @PrimaryKey val id: String,
    val name: String,
    val habitIdsCsv: String, // simple comma-separated list — a join table is overkill for V1 scale
    val bonusCredits: Long,
    val bonusXp: Long,
)
