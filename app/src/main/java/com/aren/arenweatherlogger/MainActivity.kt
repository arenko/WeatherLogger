package com.aren.arenweatherlogger

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.ogoo.heroo.service.ConnectionHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), Callback<WeatherModel>, BaseAdapterInterface {

    lateinit var rv_weather: RecyclerView
    lateinit var anim_loading: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv_weather = findViewById(R.id.rv_weather) as RecyclerView
        anim_loading = findViewById(R.id.anim_loading) as LottieAnimationView

        ConnectionHelper.api().getWeatherInfo().enqueue(this)

    }

    fun getCityName(lat:Double,lon:Double):String {
        lateinit var geocoder:Geocoder
        var address : ArrayList<Address>
        address = geocoder.getFromLocation(lat,lon,10) as ArrayList<Address>

        return ""
    }


    override fun onFailure(call: Call<WeatherModel>, t: Throwable) {

    }

    override fun onResponse(call: Call<WeatherModel>, response: Response<WeatherModel>) {
        if (response != null) {
            var weatherModel = response.body()
            if (weatherModel != null) {
                var listWeather: ArrayList<WeatherModel> = ArrayList()
                listWeather.add(weatherModel)
                createList(listWeather)
            }
        }
    }

    private fun createList(listWeather: List<WeatherModel>) {
        anim_loading.setVisibility(View.GONE)
        val layoutManager = LinearLayoutManager(this)
        rv_weather.setLayoutManager(layoutManager)

        val surveyAdapter = WeatherAdapter(listWeather, this)
        rv_weather.setAdapter(surveyAdapter)
        rv_weather.addItemDecoration(DividerItemDecoration(rv_weather.getContext(), layoutManager.getOrientation()))
    }

    override fun onAdapterItemSelectListener(`object`: Any) {

    }
}