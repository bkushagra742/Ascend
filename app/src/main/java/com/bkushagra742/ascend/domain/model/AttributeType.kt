package com.bkushagra742.ascend.domain.model

/** The 10 player attributes. Every Quest/Habit definition maps to 1+ of these (FR-ATTR-02). */
enum class AttributeType(val displayName: String) {
    STRENGTH("Strength"),
    ENDURANCE("Endurance"),
    DISCIPLINE("Discipline"),
    KNOWLEDGE("Knowledge"),
    FOCUS("Focus"),
    INTELLIGENCE("Intelligence"),
    CREATIVITY("Creativity"),
    CONSISTENCY("Consistency"),
    HEALTH("Health"),
    ENERGY("Energy")
}
