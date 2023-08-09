package it.unibs.mp.horace.models.room

import androidx.room.Embedded
import androidx.room.Relation

data class ActivityWithArea(
    @Embedded var activity: Activity,
    @Relation(parentColumn = "area_id", entityColumn = "id") var area: Area
)