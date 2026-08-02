package com.bkushagra742.ascend.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Room can't natively store Map<Enum, Int>, so attribute reward maps are stored as
 * JSON text columns. This keeps schema migrations simple (one TEXT column, not N
 * columns per attribute) at the cost of not being queryable by attribute value —
 * an acceptable tradeoff since we never need "find quests that reward Strength > 5"
 * as a SQL query; that logic lives in the domain layer over already-loaded objects.
 */
class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringIntMap(map: Map<String, Int>): String = json.encodeToString(map)

    @TypeConverter
    fun toStringIntMap(value: String): Map<String, Int> =
        if (value.isBlank()) emptyMap() else json.decodeFromString<Map<String, Int>>(value)
}
