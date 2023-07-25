package it.unibs.mp.horace.backend.journal

import it.unibs.mp.horace.models.Activity
import it.unibs.mp.horace.models.Area
import it.unibs.mp.horace.models.TimeEntry
import java.time.LocalDate

interface Journal {
    suspend fun entries(): List<TimeEntry>

    suspend fun userEntries(userId: String): List<TimeEntry>

    suspend fun addEntry(raw_entry: HashMap<String, Any>): TimeEntry

    suspend fun updateEntry(entry: TimeEntry)

    suspend fun removeEntry(entry: TimeEntry)

    suspend fun activities(): List<Activity>

    suspend fun userActivities(userId: String): List<Activity>

    suspend fun addActivity(raw_activity: HashMap<String, Any>): Activity

    suspend fun updateActivity(activity: Activity)

    suspend fun removeActivity(activity: Activity)

    suspend fun areas(): List<Area>

    suspend fun addArea(area: Area)

    suspend fun updateArea(area: Area)

    suspend fun removeArea(area: Area)

    suspend fun streak(): Int

    suspend fun increaseStreak()

    suspend fun totalActivitiesInLastWeek(): Map<LocalDate, Int> {
        return entries().filter { entry -> entry.isInCurrentWeek() }
            .groupBy { entry -> entry.startLocalDateTime().toLocalDate() }
            .mapValues { group -> group.value.size }
    }

    suspend fun activitiesFrequencyInLastWeek(): Map<Activity, Int> {
        return entries().filter { entry -> entry.activity != null && entry.isInCurrentWeek() }
            .groupBy { entry -> entry.activity!! }.mapValues { group -> group.value.size }
    }
}