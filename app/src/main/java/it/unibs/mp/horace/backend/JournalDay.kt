package it.unibs.mp.horace.backend

import it.unibs.mp.horace.models.TimeEntry
import java.time.LocalDate

data class JournalDay(
    val entries: ArrayList<TimeEntry>,
    val day: LocalDate
){
    companion object {
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
    private val totalTime: Float
        get() {
            var sum = 0.0f
            entries.forEach {
                sum += (it.duration() / 3600.0).toFloat()
            }
            return sum
        }
    val totalPoints: Int
        get() {
            var sum = 0
            entries.forEach {
                sum += it.points
            }
            return sum
        }

    fun getDayString(): String {
        val today: LocalDate = LocalDate.now()
        if (today == this.day) {
            return "Today"
        }

        if (today.minusDays(1) == this.day) {
            return "Yesterday"
        }

        return this.day.dayOfMonth.toString() + "/" + this.day.monthValue.toString() + "/" + this.day.year.toString()
    }

    fun totalTimeString(): String {
        val intTotal = totalTime.toInt()
        if (totalTime == intTotal.toFloat()) {
            return intTotal.toString()
        }
        return "%.2f".format(totalTime)
    }
}
