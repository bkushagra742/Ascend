package com.bkushagra742.ascend.domain.model

/** Maps directly to the locked naming: Missions (daily), Operations (weekly), Elite Missions. */
enum class QuestType { MISSION, OPERATION, ELITE_MISSION, CUSTOM }

enum class QuestDifficulty(val xpMultiplier: Double) {
    EASY(1.0),
    MEDIUM(1.5),
    HARD(2.2),
    BOSS(3.5) // Elite Mission territory
}

data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val type: QuestType,
    val difficulty: QuestDifficulty,
    val baseXp: Long,
    val creditReward: Long,
    val attributeRewards: Map<AttributeType, Int>,
    val isCompletedToday: Boolean,
    val isCustom: Boolean,
) {
    companion object {
        /**
         * FR-QST-04: custom quests are bounded to prevent XP-inflation exploits.
         * A user creating "Breathe air +999999 XP" should be impossible, not just
         * discouraged — enforce at construction, not just in the UI layer.
         */
        const val CUSTOM_QUEST_MAX_XP = 500L
        const val CUSTOM_QUEST_MAX_CREDITS = 200L
    }
}
