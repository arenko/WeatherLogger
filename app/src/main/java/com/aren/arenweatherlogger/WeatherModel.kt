package com.aren.arenweatherlogger

import com.google.gson.annotations.Expose

class WeatherModel {
    @Expose
    var id: Int= 0
    @Expose
    var name: String = ""
    @Expose
    var main: Main = Main()
    @Expose
    var coord: Coord = Coord()
    var savedDate: String = ""

    class Main {
        @Expose
        var temp: String = ""
        @Expose
        var pressure: String = ""
        @Expose
        var humidity: String = ""
        @Expose
        var temp_min: String = ""
        @Expose
        var temp_max: String = ""
    }

    class Coord {
        @Expose
        var lon: Double = 0.0
        @Expose
        var lat: Double = 0.0
    }

}
