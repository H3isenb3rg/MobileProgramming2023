package it.unibs.mp.horace.backend.journal

import android.content.Context
import it.unibs.mp.horace.backend.room.LocalDatabase
import it.unibs.mp.horace.backend.room.models.LocalActivity
import it.unibs.mp.horace.backend.room.models.LocalArea
import it.unibs.mp.horace.backend.room.models.LocalTimeEntry
import it.unibs.mp.horace.models.Activity
import it.unibs.mp.horace.models.Area
import it.unibs.mp.horace.models.TimeEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import java.time.LocalDateTime

/**
 * Flattens a flow list into a list.
 */
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> Flow<List<T>>.flattenToList() =
    flatMapConcat { it.asFlow() }.toList()

/**
 * A journal that uses a Room database as a backend.
 */
class RoomJournal(context: Context) : Journal {
    private val database: LocalDatabase by lazy { LocalDatabase.getInstance(context) }

    override suspend fun getAllTimeEntries(): List<TimeEntry> {
        return database.timeEntriesDao().getAllWithActivity().map { entries ->
            entries.map { it.toTimeEntry() }
        }.flattenToList()
    }

    override suspend fun getTimeEntry(id: String): TimeEntry? {
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
            activityId = activity?.id?.toLong(),
            isPomodoro = isPomodoro,
            startTime = startTime.toString(),
            endTime = endTime.toString(),
            points = points
        )
        val id = database.timeEntriesDao().insert(entry)
        return database.timeEntriesDao().getWithActivity(id).map { it.toTimeEntry() }.first()
    }

    override suspend fun updateTimeEntry(entry: TimeEntry) {
        database.timeEntriesDao()
            .update(LocalTimeEntry.fromTimeEntry(entry))
    }

    override suspend fun removeTimeEntry(entry: TimeEntry) {
        database.timeEntriesDao()
            .delete(LocalTimeEntry.fromTimeEntry(entry))
    }

    override suspend fun getAllActivities(): List<Activity> {
        return database.activitiesDao().getAllWithArea()
            .map { activities -> activities.map { it.toActivity() } }
            .flattenToList()
    }

    override suspend fun getActivity(id: String): Activity? {
        return database.activitiesDao().getWithArea(id.toLong()).map { it.toActivity() }
            .firstOrNull()
    }

    override suspend fun addActivity(name: String, area: Area?): Activity {
        val activity = LocalActivity(name = name, areaId = area?.id?.toLong())
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
        return database.areasDao().getAll()
            .map { areas -> areas.map { it.toArea() } }
            .flattenToList()
    }

    override suspend fun getArea(id: String): Area? {
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