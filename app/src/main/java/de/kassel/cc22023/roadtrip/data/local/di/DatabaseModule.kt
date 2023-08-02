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

package de.kassel.cc22023.roadtrip.data.local.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.kassel.cc22023.roadtrip.data.local.database.AppDatabase
import de.kassel.cc22023.roadtrip.data.local.database.PackingItemDao
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripActivityDao
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripDataDao
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripLocationDao
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    fun provideRoadtripDataDao(appDatabase: AppDatabase): RoadtripDataDao {
        return appDatabase.roadtripDataDao()
    }

    @Provides
    fun providePackingItemDao(appDatabase: AppDatabase): PackingItemDao {
        return appDatabase.providePackingDao()
    }

    @Provides
    fun provideRoadtripLocationDao(appDatabase: AppDatabase): RoadtripLocationDao {
        return appDatabase.roadtripLocationDao()
    }

    @Provides
    fun provideRoadtripActivityDao(appDatabase: AppDatabase): RoadtripActivityDao {
        return appDatabase.roadtripActivityDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "RoadtripPlannerCC2"
        ).build()
    }
}
