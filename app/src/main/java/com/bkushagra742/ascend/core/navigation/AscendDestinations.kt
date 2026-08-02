package com.bkushagra742.ascend.core.navigation

/**
 * Route constants for Navigation Compose. Kept as a sealed hierarchy rather than raw
 * strings scattered across composables — every screen this project adds gets one entry
 * here. Only Dashboard and Quests exist yet (this milestone's vertical slice); the rest
 * are commented as a map of what's coming so the nav graph shape is visible up front.
 */
sealed class AscendDestination(val route: String) {
    data object Dashboard : AscendDestination("dashboard")
    data object Quests : AscendDestination("quests")
    data object Habits : AscendDestination("habits")
    data object CreateQuest : AscendDestination("create_quest")
    data object CreateHabit : AscendDestination("create_habit")
    data object FocusLock : AscendDestination("focus_lock")

    // Planned, not yet implemented — kept here so route naming is decided once:
    // data object Study : AscendDestination("study")
    // data object Fitness : AscendDestination("fitness")
    // data object Statistics : AscendDestination("statistics")
    // data object Vault : AscendDestination("vault")
    // data object Shop : AscendDestination("shop")
    // data object Settings : AscendDestination("settings")
}
