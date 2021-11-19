package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.database.model.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.dto.PictureOfDay
import com.udacity.asteroidradar.api.NasaWebService
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.repository.AsteroidRepository
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val nasaWebService = NasaWebService()

    private val database = AsteroidDatabase.getInstance(application)
    private val asteroidRepository = AsteroidRepository(database)

    private val _asteroids = asteroidRepository.asteroids
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _navigateToAsteroidDetail = MutableLiveData<Asteroid>()
    val navigateToAsteroidDetail: LiveData<Asteroid>
        get() = _navigateToAsteroidDetail

    private val _pictureOfDay = MutableLiveData<PictureOfDay?>()
    val pictureOfDay: LiveData<PictureOfDay?>
        get() = _pictureOfDay

    init {
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
            _pictureOfDay.value = nasaWebService.getImageOfDay().awaitResponse().body()
        }
    }

    fun onNavigateToAsteroidDetail(asteroid: Asteroid) {
        _navigateToAsteroidDetail.value = asteroid
    }

    fun navigatingToAsteroidDetailDone() {
        _navigateToAsteroidDetail.value = null
    }
}