package de.kassel.cc22023.roadtrip.data.dependencies

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.kassel.cc22023.roadtrip.data.network.OpenAiApi
import de.kassel.cc22023.roadtrip.data.network.OpenAiApiBuilder
import de.kassel.cc22023.roadtrip.data.network.UnsplashApi
import de.kassel.cc22023.roadtrip.data.network.UnsplashApiBuilder
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun unsplashApi(): UnsplashApi {
        return UnsplashApiBuilder().unsplashApi()
    }

    @Provides
    @Singleton
    fun openAiApi(): OpenAiApi {
        return OpenAiApiBuilder().openAiApi()
    }
}