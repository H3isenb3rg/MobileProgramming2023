package it.unibs.mp.horace.backend.room.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import it.unibs.mp.horace.models.TimeEntry

/**
 * Represents a time entry in the user's journal.
 */
@Entity(
    tableName = "entries", foreignKeys = [ForeignKey(
        entity = LocalActivity::class,
        parentColumns = ["id"],
        childColumns = ["activity_id"],
        onDelete = ForeignKey.SET_NULL
    )]
)
data class LocalTimeEntry(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var description: String?,
    @ColumnInfo(name = "activity_id", index = true) var activityId: Long?,
    @ColumnInfo(name = "is_pomodoro") var isPomodoro: Boolean,
    @ColumnInfo(name = "start_time") var startTime: String,
    @ColumnInfo(name = "end_time") var endTime: String,
    var points: Int,
) {
    companion object {
        fun fromTimeEntry(entry: TimeEntry): LocalTimeEntry {
            return LocalTimeEntry(
                id = entry.id.toLong(),
                description = entry.description,
                activityId = entry.activity?.id?.toLong(),
                isPomodoro = entry.isPomodoro,
                startTime = entry.startTime.toString(),
                endTime = entry.endTime.toString(),
                points = entry.points
            )
        }
    }
}
