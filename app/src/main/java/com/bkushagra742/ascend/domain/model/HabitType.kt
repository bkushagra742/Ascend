package com.bkushagra742.ascend.domain.model

/** FR-HAB-02: negative habits track avoidance, not completion — the repository/use-case
 * layer must never apply the same "streak break = punishment" logic to both types. */
enum class HabitType { POSITIVE, NEGATIVE }

enum class HabitRecurrence { DAILY, WEEKLY, CUSTOM }
