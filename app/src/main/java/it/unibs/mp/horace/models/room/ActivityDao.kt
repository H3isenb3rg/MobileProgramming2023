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
interface ActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: Activity)

    @Update
    suspend fun update(area: Area)

    @Delete
    suspend fun delete(area: Area)

    @Query("SELECT * from activities WHERE id = :id")
    fun get(id: Int): Flow<Area>

    @Query("SELECT * from activities ORDER BY name ASC")
    fun getAll(): Flow<List<Area>>

    @Transaction
    @Query("SELECT * from activities WHERE id = :id")
    fun getWithArea(id: Int): Flow<ActivityWithArea>

    @Transaction
    @Query("SELECT * from activities ORDER BY name ASC")
    fun getAllWithArea(): Flow<List<ActivityWithArea>>
}