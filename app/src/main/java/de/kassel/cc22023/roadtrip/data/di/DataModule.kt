/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.kassel.cc22023.roadtrip.data.di

import android.content.Context
import android.hardware.SensorManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.SettingsClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.kassel.cc22023.roadtrip.data.DefaultRoadtripRepository
import de.kassel.cc22023.roadtrip.data.RoadtripRepository
import de.kassel.cc22023.roadtrip.data.local.database.PackingItem
import de.kassel.cc22023.roadtrip.data.local.database.CombinedRoadtrip
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripData
import de.kassel.cc22023.roadtrip.data.sensors.SensorRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Singleton
    @Binds
    fun bindsRoadtripRepository(
        roadtripRepository: DefaultRoadtripRepository
    ): RoadtripRepository
}