package it.unibs.mp.horace.backend.room.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Represents an activity that can be tracked.
 */
@Entity(
    tableName = "activities",
    foreignKeys = [ForeignKey(
        entity = LocalArea::class,
        parentColumns = ["id"],
        childColumns = ["area_id"],
        onDelete = ForeignKey.SET_NULL
    )]
)
data class LocalActivity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String,
    @ColumnInfo(name = "area_id", index = true) var areaId: Long?
) {
    companion object {
        fun fromActivity(activity: it.unibs.mp.horace.models.Activity): LocalActivity {
            return LocalActivity(
                name = activity.name,
                areaId = activity.area?.id?.toLong()
            )
        }
    }
}
