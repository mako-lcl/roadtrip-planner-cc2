package de.kassel.cc22023.roadtrip.data.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.kassel.cc22023.roadtrip.data.preferences.PreferenceStore
import de.kassel.cc22023.roadtrip.data.sensors.SensorRepository
import de.kassel.cc22023.roadtrip.geofence.GeofenceManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun providesSensorRepository(
        locationProvider: FusedLocationProviderClient,
    ): SensorRepository {
        return SensorRepository(locationProvider)
    }

    @Provides
    @Singleton
    fun providesPrefStore(@ApplicationContext context: Context) =
        PreferenceStore(context = context)
}