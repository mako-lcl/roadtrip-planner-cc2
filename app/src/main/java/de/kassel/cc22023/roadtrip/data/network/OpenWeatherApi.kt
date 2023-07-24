package de.kassel.cc22023.roadtrip.data.network

import de.kassel.cc22023.roadtrip.data.network.model.WeatherForLocationResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {
    @GET("weather")
    fun getWeatherForLocation(@Query("lat") lat: String, @Query("lon") lon: String, @Query("appid") apiKey: String, @Query("units") units: String) : Deferred<Response<WeatherForLocationResponse>>
}
