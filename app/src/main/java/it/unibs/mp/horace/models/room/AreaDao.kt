package it.unibs.mp.horace.models.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AreaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(area: Area)

    @Update
    suspend fun update(area: Area)

    @Delete
    suspend fun delete(area: Area)

    @Query("SELECT * from areas WHERE id = :id")
    fun get(id: Int): Flow<Area>

    @Query("SELECT * from areas ORDER BY name ASC")
    fun getAll(): Flow<List<Area>>
}