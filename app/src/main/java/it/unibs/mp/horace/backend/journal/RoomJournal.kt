package it.unibs.mp.horace.backend.journal

import it.unibs.mp.horace.models.Activity
import it.unibs.mp.horace.models.Area
import it.unibs.mp.horace.models.TimeEntry

class RoomJournal : Journal {
    override suspend fun entries(): List<TimeEntry> {
        TODO("Not yet implemented")
    }

    override suspend fun addEntry(entry: TimeEntry) {
        TODO("Not yet implemented")
    }

    override suspend fun updateEntry(entry: TimeEntry) {
        TODO("Not yet implemented")
    }

    override suspend fun removeEntry(entry: TimeEntry) {
        TODO("Not yet implemented")
    }

    override suspend fun activities(): List<Activity> {
        TODO("Not yet implemented")
    }

    override suspend fun addActivity(activity: Activity) {
        TODO("Not yet implemented")
    }

    override suspend fun updateActivity(activity: Activity) {
        TODO("Not yet implemented")
    }

    override suspend fun removeActivity(activity: Activity) {
        TODO("Not yet implemented")
    }

    override suspend fun areas(): List<Area> {
        TODO("Not yet implemented")
    }

    override suspend fun addArea(area: Area) {
        TODO("Not yet implemented")
    }

    override suspend fun updateArea(area: Area) {
        TODO("Not yet implemented")
    }

    override suspend fun removeArea(area: Area) {
        TODO("Not yet implemented")
    }
}