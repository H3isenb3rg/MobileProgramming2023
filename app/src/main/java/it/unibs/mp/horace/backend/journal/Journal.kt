package it.unibs.mp.horace.backend.journal

import it.unibs.mp.horace.models.Activity
import it.unibs.mp.horace.models.Area
import it.unibs.mp.horace.models.TimeEntry
import java.time.LocalDate
import java.time.LocalDateTime

interface Journal {
    suspend fun entries(): List<TimeEntry>

    suspend fun addEntry(entry: TimeEntry)

    suspend fun updateEntry(entry: TimeEntry)

    suspend fun removeEntry(entry: TimeEntry)

    suspend fun activities(): List<Activity>

    suspend fun addActivity(activity: Activity)

    suspend fun updateActivity(activity: Activity)

    suspend fun removeActivity(activity: Activity)

    suspend fun areas(): List<Area>

    suspend fun addArea(area: Area)

    suspend fun updateArea(area: Area)

    suspend fun removeArea(area: Area)

    suspend fun totalActivitiesInLastWeek(): Map<LocalDate, Int> {
        return entries().filter { entry -> entry.isInCurrentWeek() }
            .groupBy { entry -> LocalDateTime.parse(entry.startTime).toLocalDate() }
            .mapValues { group -> group.value.size }
    }

    suspend fun activitiesFrequencyInLastWeek(): Map<Activity, Int> {
        return entries().filter { entry -> entry.activity != null && entry.isInCurrentWeek() }
            .groupBy { entry -> entry.activity!! }.mapValues { group -> group.value.size }
    }
}