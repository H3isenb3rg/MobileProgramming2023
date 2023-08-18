package it.unibs.mp.horace.backend.journal

import it.unibs.mp.horace.backend.firebase.models.TimeEntry
import java.time.LocalDate

data class JournalDay(
    val date: LocalDate, val timeEntries: List<TimeEntry>
) {
    companion object {
        fun fromTimeEntries(entries: List<TimeEntry>): List<JournalDay> {
            // Sort entries by least recent startTime, group by date, sort by date
            // and finally map to JournalDay
            return entries.sortedBy { it.startTime.toLocalTime() }
                .groupBy { it.startTime.toLocalDate() }.toSortedMap(reverseOrder())
                .map { JournalDay(it.key, it.value) }
        }
    }

    /**
     * Total time in seconds of all the time entries
     */
    val totalTime: Int
        get() = timeEntries.sumOf { it.duration() }

    val totalPoints: Int
        get() = timeEntries.sumOf { it.points }
}
