package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.NeoWebService
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    private val neoWebService = NeoWebService()
    val asteroids = MutableLiveData<List<Asteroid>>()

    init {
        viewModelScope.launch {
            neoWebService.getAsteroids().enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val jsonResult = JSONObject(response.body() ?: "")
                    asteroids.value = parseAsteroidsJsonResult(jsonResult)
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.i("getAsteroids", t.message.toString())
                }
            })
        }
    }

}