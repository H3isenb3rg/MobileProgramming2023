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
interface TimeEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timeEntry: LocalTimeEntry)

    @Update
    suspend fun update(timeEntry: LocalTimeEntry)

    @Delete
    suspend fun delete(timeEntry: LocalTimeEntry)

    @Query("SELECT * from entries WHERE id = :id")
    fun get(id: Int): Flow<LocalTimeEntry>

    @Query("SELECT * from entries ORDER BY start_time ASC")
    fun getAll(): Flow<List<LocalTimeEntry>>

    @Transaction
    @Query("SELECT * from entries WHERE id = :id")
    fun getWithActivity(id: Int): Flow<LocalTimeEntryWithActivity>

    @Transaction
    @Query("SELECT * from entries ORDER BY start_time ASC")
    fun getAllWithActivity(): Flow<List<LocalTimeEntryWithActivity>>
}