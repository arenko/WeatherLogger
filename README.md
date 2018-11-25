# WeatherLogger

When the app starts first, it checks local data to show on the screen. You can choose if you want the weather data for current location
or any specific city. Then when you press save button it calls the api, save it locally and display it on the screen. If selected item
is "current location" weather data get refreshed periodically.

-Only Kotlin is used

-Main layout is different for phone and tablets

-When "more detils" is pressed , detail view appears with an animation

-It is possible to get weather data for different locations

-For current location weather data is retrieved periodically

-Lottie library is used for loading animation

-Widget is added, when you tap on the text in widget, it gets refreshed

-Espresso test is added to check if weather can be retrieved for Paris

-Unit test is added is spinner item number is 3 as it is in the app
