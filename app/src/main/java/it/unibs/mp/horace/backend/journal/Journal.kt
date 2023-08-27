package it.unibs.mp.horace.backend.journal

import it.unibs.mp.horace.backend.firebase.models.Activity
import it.unibs.mp.horace.backend.firebase.models.Area
import it.unibs.mp.horace.backend.firebase.models.TimeEntry
import java.time.LocalDate
import java.time.LocalDateTime

interface Journal {
    suspend fun getAllTimeEntries(): List<TimeEntry>
    suspend fun getTimeEntry(id: String): TimeEntry?
    suspend fun addTimeEntry(
        description: String?,
        activity: Activity?,
        isPomodoro: Boolean,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        points: Int
    ): TimeEntry

    suspend fun updateTimeEntry(entry: TimeEntry)
    suspend fun removeTimeEntry(entry: TimeEntry)

    suspend fun getAllActivities(): List<Activity>
    suspend fun getActivity(id: String): Activity?
    suspend fun addActivity(name: String, area: Area?): Activity
    suspend fun updateActivity(activity: Activity)
    suspend fun removeActivity(activity: Activity)

    suspend fun getAllAreas(): List<Area>
    suspend fun getArea(id: String): Area?
    suspend fun addArea(name: String): Area
    suspend fun updateArea(area: Area)
    suspend fun removeArea(area: Area)

    suspend fun streak(): Int {
        // Get the days with at least one entry
        val days =
            getAllTimeEntries().groupBy { entry -> entry.startTime.toLocalDate() }.keys.sortedDescending()

        // If there are no entries, the streak is 0
        if (days.isEmpty()) {
            return 0
        }

        // If the first day is not today, the streak is 0
        if (days.firstOrNull()?.equals(LocalDate.now()) == true) {
            return 0
        }

        // Verify the number of consecutive days
        var streak = 1
        for (i in 1 until days.size) {
            if (days[i].plusDays(1).equals(days[i - 1])) {
                streak++
            } else {
                return 0
            }
        }
        return streak
    }

    suspend fun totalHoursInLastWeek(): Map<LocalDate, Double> {
        return getAllTimeEntries().filter { entry -> entry.isInCurrentWeek }
            .groupBy { entry -> entry.startTime.toLocalDate() }
            .mapValues { group -> group.value.sumOf { it.durationInHours } }
    }

    suspend fun mostFrequentActivities(): Map<Activity, Int> {
        return getAllTimeEntries().filter { entry -> entry.activity != null }
            .groupBy { entry -> entry.activity!! }.mapValues { group -> group.value.size }
    }

    suspend fun isEmpty(): Boolean {
        return getAllTimeEntries().isEmpty() && getAllActivities().isEmpty() && getAllAreas().isEmpty()
    }
}