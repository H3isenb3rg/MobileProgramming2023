package it.unibs.mp.horace.backend.room.models

import androidx.room.Embedded
import androidx.room.Relation
import it.unibs.mp.horace.backend.firebase.models.TimeEntry
import java.time.LocalDateTime

class LocalTimeEntryWithActivity(
    @Embedded var localTimeEntry: LocalTimeEntry,
    @Relation(
        entity = LocalActivity::class,
        parentColumn = "activity_id",
        entityColumn = "id"
    ) var localActivity: LocalActivityWithArea?
) {
    fun toTimeEntry(): TimeEntry {
        return TimeEntry(
            localTimeEntry.id.toString(),
            localTimeEntry.description,
            localActivity?.toActivity(),
            localTimeEntry.isPomodoro,
            LocalDateTime.parse(localTimeEntry.startTime),
            LocalDateTime.parse(localTimeEntry.endTime),
            localTimeEntry.points
        )
    }
}