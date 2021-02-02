package com.example.uvlevel.network

import com.example.uvlevel.data.Base
import retrofit2.Call
import retrofit2.http.*

interface OpenUvApi {
    @Headers("x-access-token: 60b4b3488415041a5bb197119b6de3e2")
    @GET("api/v1/uv")
    fun getWeatherDetails(@Query("lat") lat: String,
                          @Query("lng") lng: String,
                          @Query("dt" ) dt: String): Call<Base>
}