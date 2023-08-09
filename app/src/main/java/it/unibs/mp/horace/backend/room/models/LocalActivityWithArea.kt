package it.unibs.mp.horace.backend.room.models

import androidx.room.Embedded
import androidx.room.Relation

data class LocalActivityWithArea(
    @Embedded var activity: LocalActivity,
    @Relation(parentColumn = "area_id", entityColumn = "id") var area: LocalArea
)