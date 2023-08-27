package it.unibs.mp.horace.backend.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "timer")
data class LocalTimer(@PrimaryKey(autoGenerate = false) var id: String = ID, var startTime: String) {
    companion object {
        fun fromStartTime(startTime: LocalDateTime): LocalTimer {
            return LocalTimer(startTime = startTime.toString())
        }
        const val ID = "current_start"
    }

    fun toStartTime(): LocalDateTime {
        return LocalDateTime.parse(startTime)
    }
}
