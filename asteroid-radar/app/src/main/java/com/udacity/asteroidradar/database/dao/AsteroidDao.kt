package com.udacity.asteroidradar.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.database.model.Asteroid

@Dao
interface AsteroidDao {

    @Query("SELECT * FROM asteroids ORDER BY close_approach_date DESC")
    fun getAll(): LiveData<List<Asteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg asteroids: Asteroid)

    @Query("DELETE FROM asteroids where close_approach_date < :endDate")
    suspend fun remove(endDate: String)
}
