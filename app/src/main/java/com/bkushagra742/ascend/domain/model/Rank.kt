package com.bkushagra742.ascend.domain.model

/**
 * The 8 rank tiers (locked in the design brief). Order matters — ordinal is used for
 * comparisons ("is this rank at least Gold?"), so DO NOT reorder these without a
 * Room migration for any stored ordinal values (store the name, not the ordinal, in
 * the database precisely to avoid this trap — see PlayerProfileEntity).
 */
enum class Rank(val displayName: String, val minLevel: Int) {
    BRONZE("Bronze", minLevel = 1),
    SILVER("Silver", minLevel = 10),
    GOLD("Gold", minLevel = 20),
    PLATINUM("Platinum", minLevel = 32),
    DIAMOND("Diamond", minLevel = 45),
    MASTER("Master", minLevel = 60),
    GRANDMASTER("Grandmaster", minLevel = 75),
    LEGEND("Legend", minLevel = 90);

    companion object {
        /** Resolves the correct rank for a given level. Thresholds are draft values —
         * flagged in the PRD as needing balance-pass numbers, not final game design. */
        fun forLevel(level: Int): Rank =
            entries.lastOrNull { level >= it.minLevel } ?: BRONZE
    }
}
