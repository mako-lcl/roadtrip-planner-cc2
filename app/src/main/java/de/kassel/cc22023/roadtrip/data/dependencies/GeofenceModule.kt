package de.kassel.cc22023.roadtrip.data.dependencies

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.kassel.cc22023.roadtrip.geofence.GeofenceManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GeofenceModule {
    @Provides
    @Singleton
    fun providesGeofenceManager(@ApplicationContext context: Context) : GeofenceManager {
        return GeofenceManager(context)
    }
}