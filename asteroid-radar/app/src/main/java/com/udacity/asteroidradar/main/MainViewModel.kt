package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.database.model.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.NeoWebService
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val neoWebService = NeoWebService()

    private val database = AsteroidDatabase.getInstance(application)
    private val asteroidRepository = AsteroidRepository(database)

    private val _asteroids = asteroidRepository.asteroids
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _navigateToAsteroidDetail = MutableLiveData<Asteroid>()
    val navigateToAsteroidDetail: LiveData<Asteroid>
        get() = _navigateToAsteroidDetail

    init {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        val startDate  = dateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, Constants.DEFAULT_END_DATE_DAYS)
        val endDate  = dateFormat.format(calendar.time)

        viewModelScope.launch {
            asteroidRepository.refreshAsteroids(startDate, endDate)
        }
    }

    fun onNavigateToAsteroidDetail(asteroid: Asteroid) {
        _navigateToAsteroidDetail.value = asteroid
    }

    fun navigatingToAsteroidDetailDone() {
        _navigateToAsteroidDetail.value = null
    }
}