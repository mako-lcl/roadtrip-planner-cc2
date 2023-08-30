package de.kassel.cc22023.roadtrip.data.network

import de.kassel.cc22023.roadtrip.data.network.model.UnsplashResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApi {
    @GET("search/photos")
    fun getPhotos(
        @Query("client_id") clientId: String,
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 1,
    ) : Call<UnsplashResponse>
}