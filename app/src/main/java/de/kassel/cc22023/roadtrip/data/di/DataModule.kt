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

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.kassel.cc22023.roadtrip.data.DefaultRoadtripRepository
import de.kassel.cc22023.roadtrip.data.RoadtripRepository
import de.kassel.cc22023.roadtrip.data.local.database.PackingItem
import de.kassel.cc22023.roadtrip.data.local.database.CombinedRoadtrip
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsWeatherDataRepository(
        weatherDataRepository: DefaultRoadtripRepository
    ): RoadtripRepository
}

class FakeWeatherDataRepository @Inject constructor() : RoadtripRepository {
    override val packingList: Flow<List<PackingItem>?>
        get() = TODO("Not yet implemented")

    override suspend fun updateCheckbox(item: PackingItem) {
        TODO("Not yet implemented")
    }

    override suspend fun insertIntoList(item: PackingItem) {
        TODO("Not yet implemented")
    }

    override suspend fun insertNewRoadtrip(trip: CombinedRoadtrip) {
        TODO("Not yet implemented")
    }

    override fun getRoadtrip(): CombinedRoadtrip {
        TODO("Not yet implemented")
    }

    override fun deleteItem(card: PackingItem) {
        TODO("Not yet implemented")
    }
}

val fakeWeatherDatas = RoadtripData.exampleData
