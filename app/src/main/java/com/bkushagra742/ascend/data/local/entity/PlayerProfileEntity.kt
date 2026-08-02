package com.bkushagra742.ascend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bkushagra742.ascend.data.local.Converters

/**
 * Persistence model — intentionally separate from domain.model.PlayerProfile.
 * Rank and attributes are stored as STRING keys (rank.name, attribute enum names),
 * never as ordinals, so reordering an enum in code can never silently corrupt
 * existing user data on an app update.
 */
@Entity(tableName = "player_profile")
@TypeConverters(Converters::class)
data class PlayerProfileEntity(
    @PrimaryKey val id: Long = 1L, // fixed single-row id, see PlayerProfile.SINGLE_PROFILE_ID
    val level: Int,
    val currentXp: Long,
    val credits: Long,
    val essenceStones: Long,
    val skillPoints: Int,
    val rankName: String, // Rank enum .name
    val attributesJson: String, // Map<AttributeType, Int> serialized via Converters
    val equippedAvatarId: String?,
    val equippedFrameId: String?,
    val equippedTitleId: String?,
)
