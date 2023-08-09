package it.unibs.mp.horace.models.room

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
        entity = Area::class,
        parentColumns = ["id"],
        childColumns = ["area_id"],
        onDelete = ForeignKey.SET_NULL
    )]
)
data class Activity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    @ColumnInfo(name = "area_id", index = true) val areaId: Int?
)
