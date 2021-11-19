package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.utility.SharedPreferencesUtility
import com.udacity.asteroidradar.api.NasaWebService
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.repository.AsteroidRepository
import retrofit2.HttpException
import retrofit2.awaitResponse

class RefreshDataWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params)  {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = AsteroidDatabase.getInstance(applicationContext)
        val repository = AsteroidRepository(database)
        val nasaWebService = NasaWebService()

        return try {
            repository.refreshAsteroids()
            repository.removeOldAsteroids()

            val pictureOfDay = nasaWebService.getImageOfDay().awaitResponse().body()
            pictureOfDay?.let {
                SharedPreferencesUtility.savePictureOfDayInPreferences(applicationContext, pictureOfDay)
            }

            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}