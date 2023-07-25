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

package de.kassel.cc22023.roadtrip.data

import de.kassel.cc22023.roadtrip.data.local.database.PackingItem
import de.kassel.cc22023.roadtrip.data.local.database.PackingItemDao
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripDataDao
import de.kassel.cc22023.roadtrip.data.network.OpenAiApi
import de.kassel.cc22023.roadtrip.ui.navigation.Screen
import javax.inject.Inject

interface RoadtripRepository {
    suspend fun updateCheckbox(item: PackingItem)
    suspend fun insertIntoList(item: PackingItem)
}

class DefaultWeatherDataRepository @Inject constructor(
    private val roadtripDataDao: RoadtripDataDao,
    private val packingItemDao: PackingItemDao,
    private val weatherApi: OpenAiApi
) : RoadtripRepository {
    override suspend fun updateCheckbox(item: PackingItem) {
        packingItemDao.updateCheckboxState(item)
    }



    override suspend fun insertIntoList(item: PackingItem) {
        packingItemDao.insertIntoList(item)
    }
}
