package it.unibs.mp.horace.backend.journal

import it.unibs.mp.horace.backend.firebase.models.TimeEntry
import java.time.LocalDate

data class JournalDay(
    val entries: ArrayList<TimeEntry>, val day: LocalDate
) {
    companion object {
        private const val SECONDS_IN_HOUR: Double = 3600.0
        private const val TODAY = "Today"
        private const val YESTERDAY = "Yesterday"

        fun split(entries: List<TimeEntry>): List<JournalDay> {
            val daysList = ArrayList<JournalDay>()
            if (entries.isEmpty()) {
                return daysList
            }
            val days = HashMap<String, JournalDay>()
            for (entry in entries) {
                val currDay = entry.startTime.toLocalDate()
                val currDayString = currDay.toString()
                if (days.containsKey(currDayString)) {
                    val currJournalDay = days[currDayString]!!
                    currJournalDay.entries.add(entry)
                } else {
                    val newEntries = ArrayList<TimeEntry>()
                    newEntries.add(entry)
                    days[currDayString] = JournalDay(newEntries, currDay)
                }
            }
            daysList.addAll(days.values)
            return daysList
        }
    }

    /**
     * Total time (in hours) of all the time entries
     */
    private val totalTime: Double
        get() = entries.sumOf { (it.duration() / SECONDS_IN_HOUR) }

    val totalPoints: Int
        get() = entries.sumOf { it.points }

    fun getDayString(): String {
        val today: LocalDate = LocalDate.now()
        if (today == this.day) {
            return TODAY
        }

        if (today.minusDays(1) == this.day) {
            return YESTERDAY
        }

        return this.day.dayOfMonth.toString() + "/" + this.day.monthValue.toString() + "/" + this.day.year.toString()
    }

    fun totalTimeString(): String {
        val intTotal = totalTime.toInt()
        if (totalTime == intTotal.toDouble()) {
            return intTotal.toString()
        }
        return "%.2f".format(totalTime)
    }
}
