package de.kassel.cc22023.roadtrip.data.network

import de.kassel.cc22023.roadtrip.data.network.model.OpenAiResponse
import de.kassel.cc22023.roadtrip.data.network.model.RoadtripRequest
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface OpenAiApi {
    @POST("completions")
    fun getRoadtripAsync(@Query("Content-Type") contentType: String, @Body roadtripRequest : RoadtripRequest, @Header("max_tokens") maxTokens : Int = 4096) : Deferred<Response<OpenAiResponse>>
}