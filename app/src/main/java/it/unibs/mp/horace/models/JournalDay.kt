package it.unibs.mp.horace.models

import java.time.LocalDate

data class JournalDay(
    val entries: List<TimeEntry>,
    val day: LocalDate
){
    companion object {
        fun split(entries: List<TimeEntry>): List<JournalDay> {
            TODO("Implement method to split raw list of entries")
        }
    }

    /**
     * Total time (in hours) of all the time entries
     */
    var totalTime: Float = 0.0f
    var totalPoints: Int = 0

    init {
        for (entry in entries) {
            totalTime += (entry.timeDiff() / 3600.0).toFloat()
            totalPoints += entry.points
        }
    }

    // FIXME: Totale ore togliere .00
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
}
