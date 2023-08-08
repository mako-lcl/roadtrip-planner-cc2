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

package de.kassel.cc22023.roadtrip.data.repository

import android.location.Location
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItem
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItemDao
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripDataDao
import de.kassel.cc22023.roadtrip.data.network.OpenAiApi
import de.kassel.cc22023.roadtrip.data.network.model.RoadtripRequest
import de.kassel.cc22023.roadtrip.data.network.model.RoadtripRequestMessage
import de.kassel.cc22023.roadtrip.data.repository.database.NotificationType
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripActivity
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripActivityDao
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripAndLocationsAndList
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripData
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripLocation
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripLocationDao
import de.kassel.cc22023.roadtrip.data.sensors.SensorRepository
import de.kassel.cc22023.roadtrip.util.convertCleanedStringToTrip
import de.kassel.cc22023.roadtrip.util.convertRoadtripFromTestTrip
import de.kassel.cc22023.roadtrip.util.createRoadtripPrompt
import de.kassel.cc22023.roadtrip.util.launch
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface RoadtripRepository {
    val roadtrip: Flow<RoadtripAndLocationsAndList?>

    val allRoadtrips: Flow<List<RoadtripAndLocationsAndList>?>

    suspend fun updateItem(item: PackingItem)
    suspend fun insertIntoList(item: PackingItem)
    suspend fun insertNewRoadtrip(trip: RoadtripAndLocationsAndList)

    suspend fun createRoadtrip(
        startLocation: String,
        endLocation: String,
        startDate: String,
        endDate: String,
        transportation: String,
        onSuccess: (RoadtripAndLocationsAndList) -> Unit,
        onLoading: () -> Unit,
        onError: () -> Unit
    )

    fun getRoadtrip() : RoadtripAndLocationsAndList
    fun deleteItem(card: PackingItem)
    fun getPackingList() : List<PackingItem>
    fun getLocation() : Location?
}

class DefaultRoadtripRepository @Inject constructor(
    private val roadtripDataDao: RoadtripDataDao,
    private val roadtripLocationDao: RoadtripLocationDao,
    private val roadtripActivityDao: RoadtripActivityDao,
    private val packingItemDao: PackingItemDao,
    private val openAiApi: OpenAiApi,
    private val sensorRepository: SensorRepository
) : RoadtripRepository {
    override val roadtrip: Flow<RoadtripAndLocationsAndList?>
        get() = roadtripDataDao.getRoadtripAndLocationsAsFlow()

    override val allRoadtrips: Flow<List<RoadtripAndLocationsAndList>>
        get() = roadtripDataDao.getAllRoadtripsAsFlow()

    override suspend fun insertNewRoadtrip(trip: RoadtripAndLocationsAndList) {
        roadtripDataDao.deleteRoadtrip()
        roadtripLocationDao.deleteAll()
        roadtripActivityDao.deleteAll()
        packingItemDao.deleteAllItems()

        val tripId = roadtripDataDao.insertRoadtripData(RoadtripData(0, trip.trip.startDate, trip.trip.endDate, trip.trip.startLocation, trip.trip.endLocation))

        val locations = roadtripLocationDao.insertLocations(
            trip.locations.map {loc ->
                RoadtripLocation(0, tripId, loc.location.lat, loc.location.lon, loc.location.name)
            }
        )

        trip.locations.forEachIndexed {i, loc ->
            val locationId = if (i < locations.size) locations[i] else 0
            roadtripActivityDao.insertActivities(
                loc.activities.map {
                    RoadtripActivity(0, locationId, it.name)
                }
            )
        }

        packingItemDao.insertPackingItems(
            trip.packingItems.map {
                PackingItem(
                    0,
                    tripId,
                    it.name,
                    NotificationType.NONE,
                    false,
                    null,
                    0.0,
                    0.0,
                    0.0
                )
            }
        )
    }

    override suspend fun createRoadtrip(
        startLocation: String,
        endLocation: String,
        startDate: String,
        endDate: String,
        transportation: String,
        onSuccess: (RoadtripAndLocationsAndList) -> Unit,
        onLoading: () -> Unit,
        onError: () -> Unit
    ) {
        val contentType = "application/json"
        val prompt = createRoadtripPrompt(startLocation, endLocation, startDate, endDate, transportation)
        val request = RoadtripRequest(messages = listOf(RoadtripRequestMessage(content = prompt)))
        openAiApi.getRoadtripAsync( contentType, request)
            .launch(
                onSuccess = {resp ->
                                val content = resp.choices.firstOrNull()?.message?.content
                                if (content != null) {
                                    val trip = convertCleanedStringToTrip(content)
                                    if (trip != null) {
                                        val combinedRoadtrip = convertRoadtripFromTestTrip(trip)
                                        onSuccess(combinedRoadtrip)
                                    } else {
                                        onError()
                                    }
                                } else {
                                    onError()
                                }
                            }, onLoading = {
                                onLoading()
                            }, onError = {
                                onError()
                            })
    }

    override fun getRoadtrip(): RoadtripAndLocationsAndList =
        roadtripDataDao.getRoadtripAndLocations()

    override fun deleteItem(card: PackingItem) =
        packingItemDao.deleteItem(card)

    override fun getPackingList(): List<PackingItem> =
        packingItemDao.getPackingItems()

    override fun getLocation(): Location? =
        sensorRepository.getLocation()

    override suspend fun updateItem(item: PackingItem) =
        packingItemDao.updateItem(item)

    override suspend fun insertIntoList(item: PackingItem) =
        packingItemDao.insertIntoList(item)
}
