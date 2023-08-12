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
        // TODO
        return 0
    }

    suspend fun totalActivitiesInLastWeek(): Map<LocalDate, Int> {
        return getAllTimeEntries().filter { entry -> entry.isInCurrentWeek() }
            .groupBy { entry -> entry.startTime.toLocalDate() }
            .mapValues { group -> group.value.size }
    }

    suspend fun activitiesFrequencyInLastWeek(): Map<Activity, Int> {
        return getAllTimeEntries().filter { entry -> entry.activity != null && entry.isInCurrentWeek() }
            .groupBy { entry -> entry.activity!! }.mapValues { group -> group.value.size }
    }
}