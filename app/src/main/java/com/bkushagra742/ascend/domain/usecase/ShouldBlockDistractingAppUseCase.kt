package com.bkushagra742.ascend.domain.usecase

import com.bkushagra742.ascend.domain.model.QuestType
import com.bkushagra742.ascend.domain.repository.FocusRepository
import com.bkushagra742.ascend.domain.repository.QuestRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * FR-FOC-03: the actual "should we show the overlay" decision, kept as its own use case
 * so it's unit-testable and so FocusAccessibilityService stays a thin Android adapter
 * around real logic rather than embedding business rules in a Service class.
 *
 * Current rule (V1, simple by design): block if focus lock is enabled, the foreground
 * package is in the blocked set, AND at least one Mission is still incomplete today.
 * Habits are deliberately NOT part of this check yet — mixing quest+habit completion
 * into one gate adds complexity that isn't validated as the right UX yet.
 */
class ShouldBlockDistractingAppUseCase @Inject constructor(
    private val focusRepository: FocusRepository,
    private val questRepository: QuestRepository,
) {
    suspend operator fun invoke(foregroundPackage: String): Boolean {
        val settings = focusRepository.getSettings()
        if (!settings.isEnabled) return false
        if (foregroundPackage !in settings.blockedPackages) return false

        val missions = questRepository.observeQuests(QuestType.MISSION).first()
        if (missions.isEmpty()) return false // nothing assigned yet — don't block on an empty state

        return missions.any { !it.isCompletedToday }
    }
}
