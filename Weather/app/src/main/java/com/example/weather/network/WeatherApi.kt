package com.example.weather.network

import com.example.weather.data.Base
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    fun getWeatherDetails(@Query("q") location: String,
                          @Query("APPID" )key: String): Call<Base>
}