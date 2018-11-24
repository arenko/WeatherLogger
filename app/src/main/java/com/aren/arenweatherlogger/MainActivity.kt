package com.aren.arenweatherlogger

import android.Manifest
import android.app.Dialog
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.aren.arenweatherlogger.Constants.Companion.PREFS_FILENAME
import com.aren.arenweatherlogger.Constants.Companion.PREF_CITY_NAME
import com.aren.arenweatherlogger.Constants.Companion.PREF_DATE
import com.aren.arenweatherlogger.Constants.Companion.PREF_MAX_TEMP
import com.aren.arenweatherlogger.Constants.Companion.PREF_MIN_TEMP
import com.aren.arenweatherlogger.Constants.Companion.PREF_SELECTED_ITEM
import com.aren.arenweatherlogger.Constants.Companion.PREF_TEMPERATURE
import com.aren.arenweatherlogger.Constants.Companion.PREF_WEATHER
import com.ogoo.heroo.service.ConnectionHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), Callback<WeatherModel>, BaseAdapterInterface {

    lateinit var rv_weather: RecyclerView
    lateinit var anim_loading: LottieAnimationView
    lateinit var sp_cities: Spinner
    private var locationManager: LocationManager? = null

    val dateFormatMonthDayShort = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv_weather = findViewById(R.id.rv_weather) as RecyclerView
        sp_cities = findViewById(R.id.sp_cities) as Spinner
        anim_loading = findViewById(R.id.anim_loading) as LottieAnimationView

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?;

        prefs = this.getSharedPreferences(PREFS_FILENAME, 0)

        createCitySpinner()
        createList(getSavedWeatherList())

    }

    private fun createCitySpinner() {
        val listCities = ArrayList(Arrays.asList<String>())
        listCities.add(getString(R.string.current))
        listCities.add(getString(R.string.london))
        listCities.add(getString(R.string.paris))
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listCities)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_cities.setAdapter(cityAdapter)
        sp_cities.setSelection(prefs?.getInt(Constants.PREF_SELECTED_ITEM, 0)!!)
    }

    fun getSavedWeatherList(): ArrayList<WeatherModel> {
        var cityTemperature: String? = prefs?.getString(PREF_TEMPERATURE, "")
        var cityName: String? = prefs?.getString(PREF_CITY_NAME, "")
        var savedDate: String? = prefs?.getString(PREF_DATE, "")
        var maxTemp: String? = prefs?.getString(PREF_MAX_TEMP, "")
        var minTemp: String? = prefs?.getString(PREF_MIN_TEMP, "")
        var weatherType: String? = prefs?.getString(PREF_WEATHER, "")

        var main: WeatherModel.Main = WeatherModel.Main()
        main.temp = cityTemperature!!
        main.temp_max = maxTemp!!
        main.temp_min = minTemp!!
        var weatherModel = WeatherModel()
        weatherModel.main = main
        weatherModel.name = cityName!!
        weatherModel.savedDate = savedDate!!
        weatherModel.weatherType = weatherType!!
        var listWeather: ArrayList<WeatherModel> = ArrayList()
        listWeather.add(weatherModel)

        return listWeather
    }

    fun saveWeather(weatherModel: WeatherModel) {

        val editor = prefs!!.edit()
        editor.putString(PREF_TEMPERATURE, weatherModel.main.temp)
        editor.putString(PREF_MAX_TEMP, weatherModel.main.temp_max)
        editor.putString(PREF_MIN_TEMP, weatherModel.main.temp_min)
        editor.putString(PREF_WEATHER, weatherModel.weatherType)
        editor.putString(PREF_CITY_NAME, weatherModel.name)
        editor.putString(PREF_DATE, weatherModel.savedDate)
        editor.apply()
    }


    override fun onFailure(call: Call<WeatherModel>, t: Throwable) {

    }

    override fun onResponse(call: Call<WeatherModel>, response: Response<WeatherModel>) {
        var weatherModel = response.body()
        if (weatherModel != null) {
            var weatherType = ""
            for (index in weatherModel.weather.indices) {
                weatherType += weatherModel.weather[index].main
                if (index != weatherModel.weather.size - 1) {
                    weatherType += " , "
                }
            }
            weatherModel.savedDate = dateFormatMonthDayShort.format(Calendar.getInstance().time)
            weatherModel.weatherType = weatherType
            saveWeather(weatherModel)
            var listWeather: ArrayList<WeatherModel> = ArrayList()
            listWeather.add(weatherModel)
            createList(listWeather)
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
        var weatherModel: WeatherModel = `object` as WeatherModel
        if (!weatherModel.name.isEmpty()) {
            showDetails(weatherModel)
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            var cityName: String = getCityNameByCoordinates(location.latitude, location.longitude)
            if (!cityName.isEmpty()) {
                Toast.makeText(this@MainActivity, getString(R.string.refreshed), Toast.LENGTH_SHORT).show()
                ConnectionHelper.api().getWeatherInfo(cityName).enqueue(this@MainActivity)
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    fun getCityNameByCoordinates(lat: Double, lon: Double): String {
        val geocoder = Geocoder(this)
        var cityName = ""
        try {
            var addressList: ArrayList<Address> = geocoder.getFromLocation(lat, lon, 10) as ArrayList<Address>
            for (address in addressList) {
                if (address.adminArea != null) {
                    cityName = address.adminArea
                    break
                }
            }
        } catch (e: Exception) {

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
            try {
                val editor = prefs!!.edit()
                editor.putInt(PREF_SELECTED_ITEM, sp_cities.selectedItemPosition)
                editor.apply()
                if (sp_cities.selectedItemPosition != 0) {
                    locationManager?.removeUpdates(locationListener)
                    ConnectionHelper.api().getWeatherInfo(sp_cities.selectedItem as String).enqueue(this@MainActivity)
                } else {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {
                        anim_loading.setVisibility(View.VISIBLE)
                        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener);
                    } else {
                        ActivityCompat.requestPermissions(this,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 100)
                    }
                }
            } catch (ex: SecurityException) {
                anim_loading.setVisibility(View.GONE)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("tag", "Permission has been denied by user")
                } else {
                    anim_loading.setVisibility(View.VISIBLE)
                    try {
                        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
                    } catch (ex: SecurityException) {
                        anim_loading.setVisibility(View.GONE)
                    }
                }
            }
        }
    }

    private fun showDetails(weatherModel: WeatherModel) {
        var successDialog = Dialog(this, android.R.style.Theme_NoTitleBar_Fullscreen)
        successDialog.setContentView(R.layout.layout_details)

        var tv_city_name: TextView = successDialog.findViewById(R.id.tv_city_name)
        tv_city_name.setText(weatherModel.name)
        var tv_temperature: TextView = successDialog.findViewById(R.id.tv_temperature)
        tv_temperature.setText(weatherModel.main.temp + " °C")
        var tv_max_temperature: TextView = successDialog.findViewById(R.id.tv_max_temperature)
        tv_max_temperature.setText(weatherModel.main.temp_max)
        var tv_min_temperature: TextView = successDialog.findViewById(R.id.tv_min_temperature)
        tv_min_temperature.setText(weatherModel.main.temp_min)
        var tv_weather: TextView = successDialog.findViewById(R.id.tv_weather)
        tv_weather.setText(weatherModel.weatherType)


        successDialog.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation
        successDialog.show()
    }
}