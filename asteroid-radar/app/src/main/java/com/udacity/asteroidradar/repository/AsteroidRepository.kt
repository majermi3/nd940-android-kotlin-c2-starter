package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import com.udacity.asteroidradar.database.model.Asteroid
import com.udacity.asteroidradar.api.NeoWebService
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.awaitResponse

class AsteroidRepository(private val asteroidDatabase: AsteroidDatabase) {

    val asteroids: LiveData<List<Asteroid>> = asteroidDatabase.asteroidDao.getAll()

    /**
     * Fetches new asteroids from NeoWS API and persists them in the database
     */
    suspend fun refreshAsteroids(startDate: String, endDate: String) {
        withContext(Dispatchers.IO) {
            val response = NeoWebService().getAsteroids(startDate, endDate).awaitResponse()
            val jsonResult = JSONObject(response.body() ?: "")
            val asteroids = parseAsteroidsJsonResult(jsonResult)

            insertAsteroids(asteroids)
        }
    }

    suspend fun insertAsteroids(asteroids: List<Asteroid>) {
        withContext(Dispatchers.IO) {
            asteroidDatabase.asteroidDao.insertAll(*asteroids.toTypedArray())
        }
    }

    fun removeOldAsteroids(endDate: String) {

    }
}