package it.unibs.mp.horace.backend.journal

import android.content.Context
import it.unibs.mp.horace.backend.firebase.models.Activity
import it.unibs.mp.horace.backend.firebase.models.Area
import it.unibs.mp.horace.backend.firebase.models.TimeEntry
import it.unibs.mp.horace.backend.room.LocalDatabase
import it.unibs.mp.horace.backend.room.models.LocalActivity
import it.unibs.mp.horace.backend.room.models.LocalArea
import it.unibs.mp.horace.backend.room.models.LocalTimeEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

/**
 * A journal that uses a Room database as a backend.
 */
class RoomJournal(context: Context) : Journal {
    private val database: LocalDatabase by lazy { LocalDatabase.getInstance(context) }

    override suspend fun getAllTimeEntries(): List<TimeEntry> {
        return database.timeEntriesDao().getAllWithActivity().map { entries ->
            entries.map { it.toTimeEntry() }
        }.first()
    }

    override suspend fun getTimeEntry(id: String): TimeEntry? {
        // Check if the id can be cast to a Long, otherwise return null.
        if (id.toLongOrNull() == null) {
            return null
        }

        return database.timeEntriesDao().getWithActivity(id.toLong()).map { it.toTimeEntry() }
            .firstOrNull()
    }

    override suspend fun addTimeEntry(
        description: String?,
        activity: Activity?,
        isPomodoro: Boolean,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        points: Int
    ): TimeEntry {
        val entry = LocalTimeEntry(
            description = description,
            activityId = activity?.id?.toLongOrNull(),
            isPomodoro = isPomodoro,
            startTime = startTime.toString(),
            endTime = endTime.toString(),
            points = points
        )
        val id = database.timeEntriesDao().insert(entry)
        return database.timeEntriesDao().getWithActivity(id).map { it.toTimeEntry() }.first()
    }

    override suspend fun updateTimeEntry(entry: TimeEntry) {
        database.timeEntriesDao().update(LocalTimeEntry.fromTimeEntry(entry))
    }

    override suspend fun removeTimeEntry(entry: TimeEntry) {
        database.timeEntriesDao().delete(LocalTimeEntry.fromTimeEntry(entry))
    }

    override suspend fun getAllActivities(): List<Activity> {
        return database.activitiesDao().getAllWithArea()
            .map { activities -> activities.map { it.toActivity() } }.first()
    }

    override suspend fun getActivity(id: String): Activity? {
        if (id.toLongOrNull() == null) {
            return null
        }

        return database.activitiesDao().getWithArea(id.toLong()).map { it.toActivity() }
            .firstOrNull()
    }

    override suspend fun addActivity(name: String, area: Area?): Activity {
        val activity = LocalActivity(name = name, areaId = area?.id?.toLongOrNull())
        val id = database.activitiesDao().insert(activity)
        return database.activitiesDao().getWithArea(id).map { it.toActivity() }.first()
    }

    override suspend fun updateActivity(activity: Activity) {
        database.activitiesDao().update(LocalActivity.fromActivity(activity))
    }

    override suspend fun removeActivity(activity: Activity) {
        database.activitiesDao().delete(LocalActivity.fromActivity(activity))
    }

    override suspend fun getAllAreas(): List<Area> {
        return database.areasDao().getAll().map { areas -> areas.map { it.toArea() } }.first()
    }

    override suspend fun getArea(id: String): Area? {
        if (id.toLongOrNull() == null) {
            return null
        }

        return database.areasDao().get(id.toLong()).map { it.toArea() }.firstOrNull()
    }

    override suspend fun addArea(name: String): Area {
        val area = LocalArea(name = name)
        val id = database.areasDao().insert(area)
        return database.areasDao().get(id).map { it.toArea() }.first()
    }

    override suspend fun updateArea(area: Area) {
        database.areasDao().update(LocalArea.fromArea(area))
    }

    override suspend fun removeArea(area: Area) {
        database.areasDao().delete(LocalArea.fromArea(area))
    }
}