package de.kassel.cc22023.roadtrip.data.network

import de.kassel.cc22023.roadtrip.data.network.model.OpenAiResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenAiApi {
    @GET("weather")
    fun getRoadtrip(@Query("Authorization") token: String, @Query("Content-Type") contenttype: String) : Deferred<Response<OpenAiResponse>>
}
