package it.unibs.mp.horace.backend.room.models

import androidx.room.Embedded
import androidx.room.Relation
import it.unibs.mp.horace.models.Activity

data class LocalActivityWithArea(
    @Embedded val localActivity: LocalActivity,
    @Relation(parentColumn = "area_id", entityColumn = "id") val localArea: LocalArea?
) {
    fun toActivity(): Activity {
        return Activity(localActivity.id.toString(), localActivity.name, localArea?.toArea())
    }
}