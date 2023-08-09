package it.unibs.mp.horace.backend.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "areas")
data class LocalArea(@PrimaryKey(autoGenerate = true) var id: Int, var name: String)
