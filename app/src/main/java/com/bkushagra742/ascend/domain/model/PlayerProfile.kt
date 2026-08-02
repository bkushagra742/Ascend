package com.bkushagra742.ascend.domain.model

/**
 * Domain-layer representation of the player. Deliberately has NO Room annotations —
 * that's a data-layer concern (see data/local/entity/PlayerProfileEntity.kt). The
 * mapping between the two lives in the repository implementation so domain/presentation
 * code never depends on persistence details.
 */
data class PlayerProfile(
    val id: Long = SINGLE_PROFILE_ID,
    val level: Int,
    val currentXp: Long,
    val xpToNextLevel: Long,
    val credits: Long,
    val essenceStones: Long,
    val skillPoints: Int,
    val rank: Rank,
    val attributes: Map<AttributeType, Int>,
    val equippedAvatarId: String?,
    val equippedFrameId: String?,
    val equippedTitleId: String?,
) {
    companion object {
        /** V1 supports exactly one profile per install (FR-PROF-01) — a fixed row id
         * avoids the ceremony of a real multi-user profile table for a feature that
         * doesn't exist yet. */
        const val SINGLE_PROFILE_ID = 1L

        fun newPlayer(): PlayerProfile = PlayerProfile(
            level = 1,
            currentXp = 0L,
            xpToNextLevel = XpCurve.xpRequiredForLevel(2),
            credits = 0L,
            essenceStones = 0L,
            skillPoints = 0,
            rank = Rank.BRONZE,
            attributes = AttributeType.entries.associateWith { 0 },
            equippedAvatarId = null,
            equippedFrameId = null,
            equippedTitleId = null,
        )
    }
}
