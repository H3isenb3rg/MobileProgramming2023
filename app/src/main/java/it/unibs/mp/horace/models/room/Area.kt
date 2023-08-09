package it.unibs.mp.horace.models.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "areas")
data class Area(@PrimaryKey(autoGenerate = true) var id: Int, var name: String)
