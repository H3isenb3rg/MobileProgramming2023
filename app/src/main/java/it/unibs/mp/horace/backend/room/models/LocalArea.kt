package it.unibs.mp.horace.backend.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import it.unibs.mp.horace.backend.firebase.models.Area

@Entity(tableName = "areas")
data class LocalArea(@PrimaryKey(autoGenerate = true) var id: Long = 0, var name: String) {
    companion object {
        fun fromArea(area: Area): LocalArea {
            return LocalArea(name = area.name)
        }
    }

    fun toArea(): Area {
        return Area(id.toString(), name)
    }
}
