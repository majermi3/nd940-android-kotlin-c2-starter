package com.udacity.asteroidradar.database.repository

import androidx.lifecycle.LiveData
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.database.model.Asteroid
import com.udacity.asteroidradar.api.NasaWebService
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.awaitResponse
import java.text.SimpleDateFormat
import java.util.*

class AsteroidRepository(private val asteroidDatabase: AsteroidDatabase) {

    val asteroids: LiveData<List<Asteroid>> = asteroidDatabase.asteroidDao.getAll()

    /**
     * Fetches new asteroids from NeoWS API and persists them in the database
     */
    suspend fun refreshAsteroids() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        val startDate  = dateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, Constants.DEFAULT_END_DATE_DAYS)
        val endDate  = dateFormat.format(calendar.time)

        withContext(Dispatchers.IO) {
            val response = NasaWebService().getAsteroids(startDate, endDate).awaitResponse()
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

    suspend fun removeOldAsteroids() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        val endDate  = dateFormat.format(calendar.time)

        withContext(Dispatchers.IO) {
            asteroidDatabase.asteroidDao.remove(endDate)
        }
    }
}