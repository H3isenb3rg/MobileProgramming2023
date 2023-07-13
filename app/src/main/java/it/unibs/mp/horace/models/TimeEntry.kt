package it.unibs.mp.horace.models

import java.time.LocalDateTime

data class TimeEntry(
    val description: String,
    val activity: Activity?,
    val isPomodoro: Boolean,
    val startTime: String,
    val endTime: String,
    val points: Int
) {
    // No-argument constructor required for Firestore.
    constructor() : this(
        "", null, false, LocalDateTime.now().toString(), LocalDateTime.now().toString(), 0
    )

    fun isInCurrentWeek(): Boolean {
        return LocalDateTime.parse(startTime).isAfter(LocalDateTime.now().minusWeeks(1))
    }
}