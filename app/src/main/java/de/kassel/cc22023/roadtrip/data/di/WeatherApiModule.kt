package de.kassel.cc22023.roadtrip.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.kassel.cc22023.roadtrip.data.network.OpenWeatherApi
import de.kassel.cc22023.roadtrip.data.network.OpenWeatherApiBuilder
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun openWeatherApi(): OpenWeatherApi {
        return OpenWeatherApiBuilder().openWeather()
    }
}