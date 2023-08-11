package it.unibs.mp.horace.backend.room.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import it.unibs.mp.horace.backend.room.models.LocalTimeEntry
import it.unibs.mp.horace.backend.room.models.LocalTimeEntryWithActivity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeEntriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timeEntry: LocalTimeEntry): Long

    @Update
    suspend fun update(timeEntry: LocalTimeEntry)

    @Delete
    suspend fun delete(timeEntry: LocalTimeEntry)

    @Query("SELECT * from entries WHERE id = :id")
    fun get(id: Long): Flow<LocalTimeEntry>

    @Query("SELECT * from entries ORDER BY start_time ASC")
    fun getAll(): Flow<List<LocalTimeEntry>>

    @Transaction
    @Query("SELECT * from entries WHERE id = :id")
    fun getWithActivity(id: Long): Flow<LocalTimeEntryWithActivity>

    @Transaction
    @Query("SELECT * from entries ORDER BY start_time ASC")
    fun getAllWithActivity(): Flow<List<LocalTimeEntryWithActivity>>
}