package com.aren.arenweatherlogger

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiController {
    @GET("weather?units=metric&appid=" + Constants.apiKey)
    abstract fun getWeatherInfo(@Query("q") cityName: String): Call<WeatherModel>
}