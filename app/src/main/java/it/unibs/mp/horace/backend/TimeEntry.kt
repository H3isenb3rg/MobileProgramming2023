package it.unibs.mp.horace.backend

import java.time.LocalDateTime

data class TimeEntry(
    val description: String,
    val activity: Activity?,
    val isPomodoro: Boolean,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)