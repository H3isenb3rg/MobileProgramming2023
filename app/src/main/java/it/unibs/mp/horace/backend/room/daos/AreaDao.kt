package it.unibs.mp.horace.backend.room.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import it.unibs.mp.horace.backend.room.models.LocalArea
import kotlinx.coroutines.flow.Flow

@Dao
interface AreaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(area: LocalArea)

    @Update
    suspend fun update(area: LocalArea)

    @Delete
    suspend fun delete(area: LocalArea)

    @Query("SELECT * from areas WHERE id = :id")
    fun get(id: Int): Flow<LocalArea>

    @Query("SELECT * from areas ORDER BY name ASC")
    fun getAll(): Flow<List<LocalArea>>
}