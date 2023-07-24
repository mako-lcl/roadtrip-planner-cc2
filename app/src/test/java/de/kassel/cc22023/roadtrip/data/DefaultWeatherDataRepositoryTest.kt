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

import de.kassel.cc22023.roadtrip.data.local.database.WeatherData
import de.kassel.cc22023.roadtrip.data.local.database.WeatherDataDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [DefaultWeatherDataRepository].
 */
@OptIn(ExperimentalCoroutinesApi::class) // TODO: Remove when stable
class DefaultWeatherDataRepositoryTest {

    @Test
    fun weatherDatas_newItemSaved_itemIsReturned() = runTest {
        val repository = DefaultWeatherDataRepository(FakeWeatherDataDao())

        repository.add("Repository")

        assertEquals(repository.weatherDatas.first().size, 1)
    }

}

private class FakeWeatherDataDao : WeatherDataDao {

    private val data = mutableListOf<WeatherData>()

    override fun getWeatherDatas(): Flow<List<WeatherData>> = flow {
        emit(data)
    }

    override suspend fun insertWeatherData(item: WeatherData) {
        data.add(0, item)
    }
}
