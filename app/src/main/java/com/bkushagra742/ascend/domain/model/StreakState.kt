package com.bkushagra742.ascend.domain.model

/**
 * Global (profile-level) streak state — separate from any per-habit streak tracking on
 * [Habit] itself. This is the "Daily Streak" shown on the Dashboard (FR-STR-01).
 *
 * lastQualifyingActionEpochDay: epoch-day (java.time.LocalDate.toEpochDay()) of the last
 * day a streak-qualifying action was completed. Used by the daily rollover worker to
 * detect a missed day without needing to store a full history table.
 */
data class StreakState(
    val currentStreak: Int,
    val longestStreak: Int,
    val lastQualifyingActionEpochDay: Long?,
    val freezeTokensAvailable: Int,
    val todayQualified: Boolean,
) {
    companion object {
        fun initial() = StreakState(
            currentStreak = 0,
            longestStreak = 0,
            lastQualifyingActionEpochDay = null,
            freezeTokensAvailable = 0,
            todayQualified = false,
        )
    }
}

/**
 * V1 inventory is intentionally minimal — just enough to support the Streak Saver
 * (FR-STR-02). The full Vault/Shop system (themes, frames, titles, collectibles) is a
 * separate later milestone; this is NOT that system, just its smallest useful subset.
 */
enum class InventoryItemType { STREAK_SAVER }

data class InventoryItem(
    val type: InventoryItemType,
    val quantity: Int,
)
