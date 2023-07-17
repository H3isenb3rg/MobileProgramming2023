package it.unibs.mp.horace.models

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class TimeEntry(
    val date: String,
    val startTime: String,
    val endTime: String,
    val activity: Activity?,
    val description: String?,
    val points: Int
) {
    // No-argument constructor required for Firestore.
    constructor() : this(
        LocalDate.now().toString(),
        LocalTime.now().toString(),
        LocalTime.now().toString(),
        null,
        null,
        0
    )

    fun isInCurrentWeek(): Boolean {
        return LocalDateTime.parse(startTime).isAfter(LocalDateTime.now().minusWeeks(1))
    }
}