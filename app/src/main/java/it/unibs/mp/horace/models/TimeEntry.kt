package it.unibs.mp.horace.models

import android.util.Log
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class TimeEntry(
    var id: String?,
    val description: String?,
    val activity: Activity?,
    val isPomodoro: Boolean,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val points: Int,
    val owner: User?
) {
    companion object {
        const val COLLECTION_NAME = "entries"
        const val oneWeek: Long = 7*24*3600
        const val ID_FIELD = "id"
        const val DESC_FIELD = "description"
        const val ACT_FIELD = "activity"
        const val POM_FIELD = "isPomodoro"
        const val START_FIELD = "startTime"
        const val END_FIELD = "endTime"
        const val POINTS_FIELD = "points"
        const val OWNER_FIELD = "owner"
        suspend fun parse(raw_data: Map<String, Any>): TimeEntry{
            var user: User? = null
            var desc: String? = null

            val id = raw_data[ID_FIELD].toString()
            if (raw_data.containsKey(DESC_FIELD)) {
                desc = raw_data[DESC_FIELD].toString()
            }
            // TODO: Costruttore di Activity che recupera oggetto completo
            val act = raw_data[ACT_FIELD] as Activity
            // val act = Activity("testidact", "Mobile Programming", Area("testid", "UNI"))
            val pom = raw_data[POM_FIELD] as Boolean
            val start = LocalDateTime.parse(raw_data[START_FIELD].toString())
            val end = LocalDateTime.parse(raw_data[END_FIELD].toString())
            val points = raw_data[POINTS_FIELD].toString().toInt()
            if (raw_data.containsKey(OWNER_FIELD)) {
                val rawUser = raw_data[OWNER_FIELD]
                user = if (rawUser is User) {
                    rawUser
                } else {
                    User.fetchUser(rawUser.toString())
                }
            }

            return TimeEntry(
                id, desc, act, pom, start, end, points, user
            )
        }
    }
    // No-argument constructor required for Firestore.
    constructor() : this(
        null, null, null, false, LocalDateTime.now(), LocalDateTime.now(), 0, null
    )

    fun stringify(): HashMap<String, Any> {
        val entryMap: HashMap<String, Any> = hashMapOf(
                ID_FIELD to id!!,
                ACT_FIELD to activity!!.id,
                POM_FIELD to isPomodoro,
                START_FIELD to startTime.toString(),
                END_FIELD to endTime.toString(),
                POINTS_FIELD to points,
                OWNER_FIELD to owner!!.uid
            )

        if (description != null) {
            entryMap[DESC_FIELD] = description
        }
        return entryMap
    }

    fun isInCurrentWeek(): Boolean {
        return startTime.isAfter(LocalDateTime.now().minusWeeks(1))
    }

    fun startTimeString(): String {
        val minutes = startTime.minute.toString()
        val hours = startTime.hour.toString()
        return hours + ":" + if (minutes.length < 2) "0${minutes}" else minutes
    }

    fun endTimeString(): String {
        val minutes = endTime.minute.toString()
        val hours = endTime.hour.toString()
        return hours + ":" + if (minutes.length < 2) "0${minutes}" else minutes
    }

    /**
     * Returns the duration of the time entry. By default uses unit = ChronoUnit.SECONDS
     */
    fun timeDiff(unit: ChronoUnit = ChronoUnit.SECONDS): Int {
        return unit.between(startTime, endTime).toInt()
    }

    fun timeDiffHoursFloat(): Float {
        return (timeDiff() / 3600.0).toFloat()
    }
}