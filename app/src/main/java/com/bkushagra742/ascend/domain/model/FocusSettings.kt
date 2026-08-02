package com.bkushagra742.ascend.domain.model

/**
 * FR-FOC-01: off by default, always user-revocable. [blockedPackages] is the set of
 * app package names the user has chosen to gate behind task completion — nothing is
 * blocked unless the user explicitly added it here.
 */
data class FocusSettings(
    val isEnabled: Boolean = false,
    val blockedPackages: Set<String> = emptySet(),
)

/** A launchable app on the device, for the "pick which apps to block" picker screen. */
data class InstalledApp(
    val packageName: String,
    val label: String,
)
