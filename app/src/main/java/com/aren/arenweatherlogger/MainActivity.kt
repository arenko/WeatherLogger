package com.aren.arenweatherlogger

import android.app.Dialog
import android.content.SharedPreferences
import android.location.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.ogoo.heroo.service.ConnectionHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), Callback<WeatherModel>, BaseAdapterInterface {

    lateinit var rv_weather: RecyclerView
    lateinit var anim_loading: LottieAnimationView
    private var locationManager: LocationManager? = null

    val dateFormatMonthDayShort = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    val PREFS_FILENAME = "weatherlogger.prefs"
    val PREF_TEMPERATURE = "PREF_TEMPERATURE"
    val PREF_DATE = "PREF_DATE"
    val PREF_CITY_NAME = "PREF_CITY_NAME"
    var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv_weather = findViewById(R.id.rv_weather) as RecyclerView
        anim_loading = findViewById(R.id.anim_loading)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?;

        createList(getSavedWeatherList())

    }

    fun getSavedWeatherList(): ArrayList<WeatherModel> {
        prefs = this.getSharedPreferences(PREFS_FILENAME, 0)
        var cityTemperature: String? = prefs?.getString(PREF_TEMPERATURE, "-")
        var cityName: String? = prefs?.getString(PREF_CITY_NAME, "-")
        var savedDate: String? = prefs?.getString(PREF_DATE, "-")

        var main: WeatherModel.Main = WeatherModel.Main()
        main.temp = cityTemperature!!
        var weatherModel = WeatherModel()
        weatherModel.main = main
        weatherModel.name = cityName!!
        weatherModel.savedDate = savedDate!!
        var listWeather: ArrayList<WeatherModel> = ArrayList()
        listWeather.add(weatherModel)

        return listWeather
    }

    fun saveWeather(weatherModel: WeatherModel) {
        val editor = prefs!!.edit()
        editor.putString(PREF_TEMPERATURE, weatherModel.main.temp)
        editor.putString(PREF_CITY_NAME, weatherModel.name)
        editor.putString(PREF_DATE, weatherModel.savedDate)
        editor.apply()
    }


    override fun onFailure(call: Call<WeatherModel>, t: Throwable) {

    }

    override fun onResponse(call: Call<WeatherModel>, response: Response<WeatherModel>) {
        if (response != null) {
            var weatherModel = response.body()
            if (weatherModel != null) {
                Toast.makeText(this, getString(R.string.refreshed), Toast.LENGTH_SHORT).show()
                weatherModel.savedDate = dateFormatMonthDayShort.format(Calendar.getInstance().time)
                saveWeather(weatherModel)
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

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            var cityName: String = getCityNameByCoordinates(location.latitude, location.longitude)
            ConnectionHelper.api().getWeatherInfo(cityName).enqueue(this@MainActivity)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    fun getCityNameByCoordinates(lat: Double, lon: Double): String {
        val geocoder = Geocoder(this)
        var cityName = ""
        var addressList: ArrayList<Address> = geocoder.getFromLocation(lat, lon, 10) as ArrayList<Address>
        for (address in addressList) {
            if (address.locality != null) {
                cityName = address.locality
                break
            }
        }
        return cityName
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        if (id == R.id.btn_save) {
            showDetails()
//            anim_loading.setVisibility(View.VISIBLE)
//            try {
//                locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener);
//            } catch (ex: SecurityException) {
//                Log.d("tag", "No location available");
//            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDetails() {
        var successDialog = Dialog(this, android.R.style.Theme_NoTitleBar_Fullscreen)
        successDialog.setContentView(R.layout.layout_details)


        successDialog.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation
        successDialog.show()
    }
}