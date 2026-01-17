package com.mosec.tpsuite.data

import android.content.Context
import androidx.room.*
import androidx.room.Database

@Database(entities = [DataLog::class, User::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dataLogDao(): DataLogDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tp_suite_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
