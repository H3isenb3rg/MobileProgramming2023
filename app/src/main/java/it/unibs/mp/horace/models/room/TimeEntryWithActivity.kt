package it.unibs.mp.horace.models.room

import androidx.room.Embedded
import androidx.room.Relation

class TimeEntryWithActivity(
    @Embedded var timeEntry: TimeEntry,
    @Relation(parentColumn = "activity_id", entityColumn = "id") var activity: Activity
)