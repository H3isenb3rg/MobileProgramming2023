package it.unibs.mp.horace.models.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timeEntry: TimeEntry)

    @Update
    suspend fun update(timeEntry: TimeEntry)

    @Delete
    suspend fun delete(timeEntry: TimeEntry)

    @Query("SELECT * from entries WHERE id = :id")
    fun get(id: Int): Flow<TimeEntry>

    @Query("SELECT * from entries ORDER BY start_time ASC")
    fun getAll(): Flow<List<TimeEntry>>

    @Transaction
    @Query("SELECT * from entries WHERE id = :id")
    fun getWithActivity(id: Int): Flow<TimeEntryWithActivity>

    @Transaction
    @Query("SELECT * from entries ORDER BY start_time ASC")
    fun getAllWithActivity(): Flow<List<TimeEntryWithActivity>>
}