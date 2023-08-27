package it.unibs.mp.horace.backend.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import it.unibs.mp.horace.backend.room.models.LocalTimer
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(status: LocalTimer): Long

    @Update
    suspend fun update(status: LocalTimer)

    @Query("DELETE FROM timer")
    suspend fun delete()

    @Query("SELECT * from timer WHERE id = :id")
    fun get(id: String): Flow<LocalTimer>
}