package it.unibs.mp.horace.backend.journal

import it.unibs.mp.horace.models.Activity
import it.unibs.mp.horace.models.Area
import it.unibs.mp.horace.models.TimeEntry

class RoomJournal : Journal {
    override suspend fun userEntries(userId: String): List<TimeEntry> {
        TODO("Not yet implemented")
    }

    override suspend fun addEntry(raw_entry: HashMap<String, Any>): TimeEntry {
        TODO("Not yet implemented")
    }

    override suspend fun updateEntry(entry: TimeEntry) {
        TODO("Not yet implemented")
    }

    override suspend fun removeEntry(entry: TimeEntry) {
        TODO("Not yet implemented")
    }

    override suspend fun userActivities(userId: String): List<Activity> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserActivity(activityID: String, userID: String): Activity {
        TODO("Not yet implemented")
    }

    override suspend fun addActivity(raw_activity: HashMap<String, Any>): Activity {
        TODO("Not yet implemented")
    }

    override suspend fun updateActivity(activity: Activity) {
        TODO("Not yet implemented")
    }

    override suspend fun removeActivity(activity: Activity) {
        TODO("Not yet implemented")
    }

    override suspend fun userAreas(uid: String): List<Area> {
        TODO("Not yet implemented")
    }

    override suspend fun addArea(name: String): Area {
        TODO("Not yet implemented")
    }

    override suspend fun updateArea(area: Area) {
        TODO("Not yet implemented")
    }

    override suspend fun removeArea(area: Area) {
        TODO("Not yet implemented")
    }

    override suspend fun getUserArea(userID: String, areaID: String): Area {
        TODO("Not yet implemented")
    }

    override suspend fun streak(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun increaseStreak() {
        TODO("Not yet implemented")
    }
}