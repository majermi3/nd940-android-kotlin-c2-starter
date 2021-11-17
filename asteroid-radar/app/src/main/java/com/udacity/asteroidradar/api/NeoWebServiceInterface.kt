package com.udacity.asteroidradar.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NeoWebServiceInterface {

    @GET("feed")
    fun getAsteroids(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String
    ): Call<String>
}