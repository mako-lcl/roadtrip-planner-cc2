package de.kassel.cc22023.roadtrip.data.network.model

import com.squareup.moshi.Json

data class WeatherForLocationResponse(
    @Json(name = "main")
    var weatherForecastForLocation: WeatherMainResponse,
    @Json(name = "wind")
    var weatherWind: WeatherWind,
    @Json(name = "weather")
    var weather: List<Weather>,
    @Json(name = "sys")
    var sys: SystemData,
    var name: String?,
    var icon: String?,
    @Json(name = "dt")
    var timestamp: Long?
)
data class WeatherWind(
    var speed: Double?,
    var deg: Double?,
    var gust: Double?
)
data class Weather(
    var id: Int?,
    var main: String?,
    var description: String?,
    var icon: String?
)
data class SystemData(
    var sunrise: Long?,
    var sunset: Long?

)
data class WeatherMainResponse(
    var temp: Double?,
    @Json(name = "feels_like")
    var feelsLike: Double?,
    @Json(name = "temp_min")
    var tempMin: Double?,
    @Json(name = "temp_max")
    var tempMax: Double?,
    var pressure: Int?,
    var humidity: Int?,
    @Json(name = "sea_level")
    var seaLevel: Int?,
    @Json(name = "grnd_level")
    var groundLevel: Int?
)