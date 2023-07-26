package de.kassel.cc22023.roadtrip.data.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.kassel.cc22023.roadtrip.BuildConfig
import okhttp3.Interceptor
import okhttp3.Interceptor.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


class OpenAiApiBuilder {
    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    fun openAiApi(): OpenAiApi =
        Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/chat/")
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(initOkHttp())
            .build()
            .create(OpenAiApi::class.java)

    private fun initOkHttp(): OkHttpClient {
        val client = OkHttpClient.Builder()

        client.connectTimeout(15, TimeUnit.SECONDS)
        client.readTimeout(300, TimeUnit.SECONDS)

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        client.addInterceptor(loggingInterceptor)

        client.addInterceptor(TokenInterceptor())

        return client.build()
    }
}

class TokenInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Chain): Response {
        val token = BuildConfig.GPT_KEY

        //rewrite the request to add bearer token
        val newRequest: Request = chain.request().newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(newRequest)
    }
}