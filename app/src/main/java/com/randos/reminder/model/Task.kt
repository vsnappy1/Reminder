package com.randos.reminder.model

import com.randos.reminder.enums.RepeatCycle
import java.time.LocalDate
import java.time.LocalTime

data class Task(
    val title: String,
    val notes: String,
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val repeat: RepeatCycle = RepeatCycle.NO_REPEAT,
    val done: Boolean = false
)
