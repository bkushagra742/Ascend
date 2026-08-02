package com.bkushagra742.ascend.domain.model

/**
 * Preset habit templates shown when a user adds a habit — covers the common
 * self-improvement targets (digital wellbeing, diet, focus) so someone doesn't have to
 * type these out themselves. Negative habits here are framed as things to AVOID, and
 * completing them means "I successfully avoided this today" (FR-HAB-02) — the UI must
 * never present a missed day on one of these as a punishment, just a reset.
 */
object HabitTemplates {

    val positive: List<HabitTemplate> = listOf(
        HabitTemplate("Wake Up Early", "Start the day before 7 AM", HabitType.POSITIVE, mapOf(AttributeType.DISCIPLINE to 2)),
        HabitTemplate("Drink Water", "8 glasses of water today", HabitType.POSITIVE, mapOf(AttributeType.HEALTH to 1)),
        HabitTemplate("Read a Book", "20 minutes of reading", HabitType.POSITIVE, mapOf(AttributeType.KNOWLEDGE to 2)),
        HabitTemplate("Meditate", "10 minutes of quiet focus", HabitType.POSITIVE, mapOf(AttributeType.FOCUS to 2)),
        HabitTemplate("Exercise", "Any movement counts", HabitType.POSITIVE, mapOf(AttributeType.STRENGTH to 2, AttributeType.ENDURANCE to 1)),
        HabitTemplate("Journal", "Write down today's thoughts", HabitType.POSITIVE, mapOf(AttributeType.CREATIVITY to 1)),
    )

    val negative: List<HabitTemplate> = listOf(
        HabitTemplate("No Phone Before Bed", "No screens in the last hour before sleep", HabitType.NEGATIVE, mapOf(AttributeType.DISCIPLINE to 2, AttributeType.FOCUS to 1)),
        HabitTemplate("No Sugar", "Avoid added sugar today", HabitType.NEGATIVE, mapOf(AttributeType.HEALTH to 2, AttributeType.DISCIPLINE to 1)),
        HabitTemplate("No Porn", "Stay off adult content today", HabitType.NEGATIVE, mapOf(AttributeType.DISCIPLINE to 3, AttributeType.FOCUS to 1)),
        HabitTemplate("No Smoking", "Stay smoke-free today", HabitType.NEGATIVE, mapOf(AttributeType.HEALTH to 3)),
        HabitTemplate("No Junk Food", "Skip fast food / processed snacks", HabitType.NEGATIVE, mapOf(AttributeType.HEALTH to 2)),
        HabitTemplate("No Social Media Doomscroll", "No mindless scrolling today", HabitType.NEGATIVE, mapOf(AttributeType.FOCUS to 2, AttributeType.CONSISTENCY to 1)),
    )
}

data class HabitTemplate(
    val title: String,
    val description: String,
    val type: HabitType,
    val attributeRewards: Map<AttributeType, Int>,
)
