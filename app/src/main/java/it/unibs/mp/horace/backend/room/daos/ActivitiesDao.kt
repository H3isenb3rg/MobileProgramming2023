package it.unibs.mp.horace.backend.room.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import it.unibs.mp.horace.backend.room.models.LocalActivity
import it.unibs.mp.horace.backend.room.models.LocalActivityWithArea
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivitiesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: LocalActivity): Long

    @Update
    suspend fun update(area: LocalActivity)

    @Delete
    suspend fun delete(area: LocalActivity)

    @Query("SELECT * from activities WHERE id = :id")
    fun get(id: Long): Flow<LocalActivity>

    @Query("SELECT * from activities ORDER BY name ASC")
    fun getAll(): Flow<List<LocalActivity>>

    @Transaction
    @Query("SELECT * from activities WHERE id = :id")
    fun getWithArea(id: Long): Flow<LocalActivityWithArea>

    @Transaction
    @Query("SELECT * from activities ORDER BY name ASC")
    fun getAllWithArea(): Flow<List<LocalActivityWithArea>>
}