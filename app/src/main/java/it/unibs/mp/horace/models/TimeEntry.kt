package it.unibs.mp.horace.models

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset

data class TimeEntry(
    var _id: String?,
    val description: String,
    val activity: Activity?,
    val isPomodoro: Boolean,
    val startTime: Timestamp,
    val endTime: Timestamp,
    val points: Int,
    val owner: User?
) {
    companion object {
        const val oneWeek: Long = 7*24*3600
        const val ID_FIELD = "_id"
        const val DESC_FIELD = "description"
        const val ACT_FIELD = "activity"
        const val POM_FIELD = "isPomodoro"
        const val START_FIELD = "startTime"
        const val END_FIELD = "endTime"
        const val POINTS_FIELD = "points"
        const val OWNER_FIELD = "owner"
        fun parse(raw_data: Map<String, Any>): TimeEntry{
            val id = raw_data[ID_FIELD].toString()
            val desc = raw_data[DESC_FIELD].toString()
            // TODO: Costruttore di Activity che recupera oggetto completo
            // val act = raw_data[ACT_FIELD]
            val act = Activity("Mobile Programming", Area("UNI"))
            val pom = raw_data[POM_FIELD] as Boolean
            val start = raw_data[START_FIELD] as Timestamp
            val end = raw_data[END_FIELD] as Timestamp
            val points = raw_data[POINTS_FIELD] as Long
            // TODO: Costruttore di User che recupera oggetto completo
            // val user = raw_data[OWNER_FIELD]
            val user = User(raw_data[OWNER_FIELD].toString(), "matteo@mail.com", "matteo")
            return TimeEntry(
                id, desc, act, pom, start, end, points.toInt(), user
            )
        }
    }
    // No-argument constructor required for Firestore.
    constructor() : this(
        null, "", null, false, Timestamp.now(), Timestamp.now(), 0, null
    )

    fun stringify(): HashMap<String, Any> {
        return hashMapOf(
            ID_FIELD to _id!!,
            DESC_FIELD to description,
            ACT_FIELD to activity!!.name,
            POM_FIELD to isPomodoro,
            START_FIELD to startTime,
            END_FIELD to endTime,
            POINTS_FIELD to points,
            OWNER_FIELD to owner!!.uid
        )
    }

    fun isInCurrentWeek(): Boolean {
        return startTime.seconds > (Timestamp.now().seconds - oneWeek)
    }

    fun startTimeString(): String {
        val minutes = startLocalDateTime().minute.toString()
        val hours = startLocalDateTime().hour.toString()
        return hours + ":" + if (minutes.length < 2) "0${minutes}" else minutes
    }

    fun endTimeString(): String {
        val minutes = endLocalDateTime().minute.toString()
        val hours = endLocalDateTime().hour.toString()
        return hours + ":" + if (minutes.length < 2) "0${minutes}" else minutes
    }

    fun timeDiffFloat(): Float {
        return ((endTime.seconds - startTime.seconds)/3600.0).toFloat()
    }

    fun startLocalDateTime(): LocalDateTime {
        return LocalDateTime.ofEpochSecond(this.startTime.seconds, this.startTime.nanoseconds, ZoneOffset.UTC)
    }

    fun endLocalDateTime(): LocalDateTime {
        return LocalDateTime.ofEpochSecond(this.endTime.seconds, this.endTime.nanoseconds, ZoneOffset.UTC)
    }
}