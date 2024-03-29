package it.unibs.mp.horace.backend.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import it.unibs.mp.horace.backend.room.daos.ActivitiesDao
import it.unibs.mp.horace.backend.room.daos.AreasDao
import it.unibs.mp.horace.backend.room.daos.TimeEntriesDao
import it.unibs.mp.horace.backend.room.models.LocalActivity
import it.unibs.mp.horace.backend.room.models.LocalArea
import it.unibs.mp.horace.backend.room.models.LocalTimeEntry

@Database(
    entities = [LocalArea::class, LocalActivity::class, LocalTimeEntry::class],
    version = 1,
    exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun areasDao(): AreasDao
    abstract fun activitiesDao(): ActivitiesDao
    abstract fun timeEntriesDao(): TimeEntriesDao

    companion object {
        private const val DB_NAME = "horace_database"

        @Volatile
        private var INSTANCE: LocalDatabase? = null

        fun getInstance(context: Context): LocalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}