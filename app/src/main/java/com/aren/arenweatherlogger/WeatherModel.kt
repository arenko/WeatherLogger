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
        var pressure: Double = 0.0
        @Expose
        var humidity: Double = 0.0
        @Expose
        var temp_min: Double = 0.0
        @Expose
        var temp_max: Double = 0.0
    }

    class Coord {
        @Expose
        var lon: Double = 0.0
        @Expose
        var lat: Double = 0.0
    }

}
