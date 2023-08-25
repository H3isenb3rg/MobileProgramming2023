package it.unibs.mp.horace.backend.firebase.models

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Represents a time entry in the user's journal.
 */
data class TimeEntry(
    val id: String,
    var description: String?,
    var activity: Activity?,
    val isPomodoro: Boolean,
    var startTime: LocalDateTime,
    var endTime: LocalDateTime,
    val points: Int,
) {
    companion object {
        const val COLLECTION_NAME = "entries"

        const val ID_FIELD = "id"
        const val DESCRIPTION_FIELD = "description"
        const val ACTIVITY_FIELD = "activity"
        const val POMODORO_FIELD = "isPomodoro"
        const val START_FIELD = "startTime"
        const val END_FIELD = "endTime"
        const val POINTS_FIELD = "points"

        /**
         * Parse a map of data into a TimeEntry object.
         */
        fun parse(data: Map<String, Any>): TimeEntry {
            val id = data[ID_FIELD].toString()
            val description = if (data.containsKey(DESCRIPTION_FIELD)) {
                data[DESCRIPTION_FIELD].toString()
            } else {
                null
            }
            val activity = data[ACTIVITY_FIELD] as Activity
            val isPomodoro = data[POMODORO_FIELD] as Boolean
            val start = LocalDateTime.parse(data[START_FIELD].toString())
            val end = LocalDateTime.parse(data[END_FIELD].toString())
            val points = data[POINTS_FIELD].toString().toInt()

            return TimeEntry(
                id, description, activity, isPomodoro, start, end, points
            )
        }
    }

    // No-argument constructor required for Firestore.
    @Suppress("unused")
    constructor() : this(
        "", null, null, false, LocalDateTime.now(), LocalDateTime.now(), 0
    )

    /**
     * Convert the time entry to a map of data.
     */
    fun stringify(): HashMap<String, Any> {
        val entryMap: HashMap<String, Any> = hashMapOf(
            ID_FIELD to id,
            POMODORO_FIELD to isPomodoro,
            START_FIELD to startTime.toString(),
            END_FIELD to endTime.toString(),
            POINTS_FIELD to points,
        )

        description?.let {
            entryMap[DESCRIPTION_FIELD] = it
        }

        activity?.let {
            entryMap[ACTIVITY_FIELD] = it.id
        }

        return entryMap
    }

    /**
     * Whether the time entry is in the current week.
     */
    val isInCurrentWeek: Boolean
        get() = startTime.isAfter(LocalDateTime.now().minusWeeks(1))

    /**
     * The duration of the time entry in hours.
     */
    val durationInHours: Double
        get() = duration().toDouble() / 3600.0f

    /**
     * The duration of the time entry in the given unit. By default, the unit is seconds.
     */
    fun duration(unit: ChronoUnit = ChronoUnit.SECONDS): Int {
        return unit.between(startTime, endTime).toInt()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is TimeEntry) {
            return false
        }
        if (id == other.id) {
            return true
        }
        return description == other.description &&
                activity == other.activity &&
                isPomodoro == other.isPomodoro &&
                startTime == other.startTime &&
                endTime == other.endTime &&
                points == other.points
    }

    override fun hashCode(): Int {
        var result = description?.hashCode() ?: 0
        result = 31 * result + (activity?.hashCode() ?: 0)
        result = 31 * result + isPomodoro.hashCode()
        result = 31 * result + startTime.hashCode()
        result = 31 * result + endTime.hashCode()
        result = 31 * result + points
        return result
    }
}