package com.randos.reminder.enums

enum class RepeatCycle(val value: String) {
    NO_REPEAT("No repeat"),
    HOURLY("Hourly"),
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly")
}

enum class Priority(val value: String) {
    NONE("None"), LOW("Low"), MEDIUM("Medium"), HIGH("High");
}

enum class ReminderScreen {
    ADD_TASK_SCREEN,
    EDIT_TASK_SCREEN,
    TODAY_TASK_SCREEN,
    SCHEDULED_TASK_SCREEN,
    COMPLETED_TASK_SCREEN,
    ALL_TASK_SCREEN,
    HOME_SCREEN
}