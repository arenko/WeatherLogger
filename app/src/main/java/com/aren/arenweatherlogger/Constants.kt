package com.aren.arenweatherlogger

class Constants {
    companion object {
        const val apiURL = "http://api.openweathermap.org/data/2.5/";
        const val apiKey = "f6dc777b36d5fad7010aa4d0133c4f52";
        const val PREFS_FILENAME = "weatherlogger.prefs"
        const val PREF_TEMPERATURE = "PREF_TEMPERATURE"
        const val PREF_DATE = "PREF_DATE"
        const val PREF_CITY_NAME = "PREF_CITY_NAME"
        const val PREF_MAX_TEMP = "PREF_MAX_TEMP"
        const val PREF_MIN_TEMP = "PREF_MIN_TEMP"
        const val PREF_WEATHER = "PREF_WEATHER"
        const val PREF_SELECTED_ITEM = "PREF_SELECTED_ITEM"
    }
}