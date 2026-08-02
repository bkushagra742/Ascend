package com.bkushagra742.ascend.data.repository

import com.bkushagra742.ascend.data.local.dao.PlayerProfileDao
import com.bkushagra742.ascend.data.local.entity.PlayerProfileEntity
import com.bkushagra742.ascend.domain.model.AttributeType
import com.bkushagra742.ascend.domain.model.PlayerProfile
import com.bkushagra742.ascend.domain.model.Rank
import com.bkushagra742.ascend.domain.model.XpCurve
import com.bkushagra742.ascend.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepositoryImpl @Inject constructor(
    private val dao: PlayerProfileDao,
) : PlayerRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override fun observeProfile(): Flow<PlayerProfile> =
        dao.observeProfile().filterNotNull().map { it.toDomain() }

    override suspend fun getProfile(): PlayerProfile =
        dao.getProfile()?.toDomain() ?: run {
            createProfileIfAbsent()
            dao.getProfile()!!.toDomain()
        }

    override suspend fun createProfileIfAbsent() {
        if (dao.getProfile() == null) {
            dao.insert(PlayerProfile.newPlayer().toEntity())
        }
    }

    override suspend fun applyRewards(
        xpGained: Long,
        creditsGained: Long,
        essenceGained: Long,
        attributeGains: Map<AttributeType, Int>,
    ) {
        val current = dao.getProfile() ?: run { createProfileIfAbsent(); dao.getProfile()!! }

        val newCumulativeXp = current.currentXp + xpGained
        val newLevel = XpCurve.levelForCumulativeXp(newCumulativeXp).coerceAtMost(XpCurve.MAX_LEVEL)
        val newRank = Rank.forLevel(newLevel)

        val currentAttrs = current.attributesJson.let {
            if (it.isBlank()) emptyMap() else json.decodeFromString<Map<String, Int>>(it)
        }.toMutableMap()
        attributeGains.forEach { (type, gain) ->
            currentAttrs[type.name] = (currentAttrs[type.name] ?: 0) + gain
        }

        dao.update(
            current.copy(
                currentXp = newCumulativeXp,
                level = newLevel,
                rankName = newRank.name,
                credits = current.credits + creditsGained,
                essenceStones = current.essenceStones + essenceGained,
                attributesJson = json.encodeToString(currentAttrs),
            )
        )
    }

    override suspend fun spendCredits(amount: Long): Boolean {
        val current = dao.getProfile() ?: return false
        if (current.credits < amount) return false
        dao.update(current.copy(credits = current.credits - amount))
        return true
    }

    override suspend fun spendEssenceStones(amount: Long): Boolean {
        val current = dao.getProfile() ?: return false
        if (current.essenceStones < amount) return false
        dao.update(current.copy(essenceStones = current.essenceStones - amount))
        return true
    }

    private fun PlayerProfileEntity.toDomain(): PlayerProfile {
        val attrs = if (attributesJson.isBlank()) emptyMap()
        else json.decodeFromString<Map<String, Int>>(attributesJson)

        return PlayerProfile(
            id = id,
            level = level,
            currentXp = currentXp,
            xpToNextLevel = XpCurve.xpForNextLevel(level),
            credits = credits,
            essenceStones = essenceStones,
            skillPoints = skillPoints,
            rank = Rank.valueOf(rankName),
            attributes = AttributeType.entries.associateWith { attrs[it.name] ?: 0 },
            equippedAvatarId = equippedAvatarId,
            equippedFrameId = equippedFrameId,
            equippedTitleId = equippedTitleId,
        )
    }

    private fun PlayerProfile.toEntity(): PlayerProfileEntity = PlayerProfileEntity(
        id = id,
        level = level,
        currentXp = currentXp,
        credits = credits,
        essenceStones = essenceStones,
        skillPoints = skillPoints,
        rankName = rank.name,
        attributesJson = json.encodeToString(attributes.mapKeys { it.key.name }),
        equippedAvatarId = equippedAvatarId,
        equippedFrameId = equippedFrameId,
        equippedTitleId = equippedTitleId,
    )
}
