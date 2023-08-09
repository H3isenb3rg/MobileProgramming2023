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
import it.unibs.mp.horace.backend.room.models.LocalArea
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: LocalActivity)

    @Update
    suspend fun update(area: LocalArea)

    @Delete
    suspend fun delete(area: LocalArea)

    @Query("SELECT * from activities WHERE id = :id")
    fun get(id: Int): Flow<LocalArea>

    @Query("SELECT * from activities ORDER BY name ASC")
    fun getAll(): Flow<List<LocalArea>>

    @Transaction
    @Query("SELECT * from activities WHERE id = :id")
    fun getWithArea(id: Int): Flow<LocalActivityWithArea>

    @Transaction
    @Query("SELECT * from activities ORDER BY name ASC")
    fun getAllWithArea(): Flow<List<LocalActivityWithArea>>
}