package it.unibs.mp.horace.backend.room.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

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
    @PrimaryKey(autoGenerate = true) var id: String,
    val description: String?,
    @ColumnInfo(name = "activity_id", index = true) val activityId: Int?,
    @ColumnInfo(name = "is_pomodoro") val isPomodoro: Boolean,
    @ColumnInfo(name = "start_time") val startTime: LocalDateTime,
    @ColumnInfo(name = "end_time") val endTime: LocalDateTime,
    val points: Int,
)
