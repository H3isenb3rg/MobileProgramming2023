package it.unibs.mp.horace.backend.journal

import it.unibs.mp.horace.models.Activity
import it.unibs.mp.horace.models.Area
import it.unibs.mp.horace.models.TimeEntry
import java.time.LocalDate

interface Journal {
    suspend fun getCurrentUid(): String {
        return "local_user"
    }

    /**
     * Returns a List of [TimeEntry] of all the time entries of the current user.
     *
     * (Should call [userEntries] with the current logged user id)
     */
    suspend fun entries(): List<TimeEntry>

    /**
     * Returns a List of [TimeEntry] of all the time entries of the specified user
     */
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

    suspend fun userAreas(uid: String): List<Area>

    suspend fun addArea(name: String): Area

    suspend fun updateArea(area: Area)

    suspend fun removeArea(area: Area)

    suspend fun getArea(areaID: String): Area

    suspend fun getUserArea(userID: String, areaID: String): Area

    suspend fun streak(): Int

    suspend fun increaseStreak()

    suspend fun totalActivitiesInLastWeek(): Map<LocalDate, Int> {
        return entries().filter { entry -> entry.isInCurrentWeek() }
            .groupBy { entry -> entry.startTime.toLocalDate() }
            .mapValues { group -> group.value.size }
    }

    suspend fun activitiesFrequencyInLastWeek(): Map<Activity, Int> {
        return entries().filter { entry -> entry.activity != null && entry.isInCurrentWeek() }
            .groupBy { entry -> entry.activity!! }.mapValues { group -> group.value.size }
    }
}