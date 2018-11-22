package com.ogoo.heroo.service

import com.aren.arenweatherlogger.ApiController
import com.aren.arenweatherlogger.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ConnectionHelper {

    companion object Factory {
        val BASE_URL = Constants.apiURL
        fun api(): ApiController {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiController::class.java)
        }
    }
}