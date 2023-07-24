package de.kassel.cc22023.roadtrip.data.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class OpenAiApiBuilder {
    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    fun openAiApi(): OpenAiApi =
        Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/chat/completions")
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(initOkHttp())
            .build()
            .create(OpenAiApi::class.java)

    private fun initOkHttp(): OkHttpClient {
        val client = OkHttpClient.Builder()

        client.connectTimeout(15, TimeUnit.SECONDS)
        client.readTimeout(15, TimeUnit.SECONDS)

        // TODO: check if debug variant and only intercept if in debug
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        client.addInterceptor(loggingInterceptor)

        return client.build()
    }
}