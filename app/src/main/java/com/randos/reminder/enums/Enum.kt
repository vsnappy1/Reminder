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

enum class ReminderScreen{
    REMINDER_LIST, ADD_AND_MODIFY_TASK_SCREEN
}