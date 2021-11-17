package com.udacity.asteroidradar.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.udacity.asteroidradar.database.dao.AsteroidDao
import com.udacity.asteroidradar.database.model.Asteroid

@Database(entities = [Asteroid::class], version = 1)
abstract class AsteroidDatabase : RoomDatabase() {

    abstract val asteroidDao: AsteroidDao

    companion object {

        @Volatile
        private lateinit var INSTANCE: AsteroidDatabase

        fun getInstance(context: Context): AsteroidDatabase {
            synchronized(AsteroidDatabase::class.java) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AsteroidDatabase::class.java,
                        "asteroids"
                    ).build()
                }
            }

            return INSTANCE
        }
    }
}
