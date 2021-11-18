package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.PictureOfDay
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaWebServiceInterface {

    @GET("neo/rest/v1/feed")
    fun getAsteroids(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String
    ): Call<String>

    @GET("planetary/apod")
    fun getPictureOfDay(@Query("api_key") apiKey: String): Call<PictureOfDay>
}