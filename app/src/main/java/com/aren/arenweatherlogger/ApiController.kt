package com.aren.arenweatherlogger

import retrofit2.Call
import retrofit2.http.GET

interface ApiController {
    @GET("weather?q=istanbul&units=metric&appid=f6dc777b36d5fad7010aa4d0133c4f52")
    abstract fun getWeatherInfo(): Call<WeatherModel>
}