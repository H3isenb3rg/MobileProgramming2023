package it.unibs.mp.horace.backend.room.models

import androidx.room.Embedded
import androidx.room.Relation

class LocalTimeEntryWithActivity(
    @Embedded var timeEntry: LocalTimeEntry,
    @Relation(parentColumn = "activity_id", entityColumn = "id") var activity: LocalActivity
)