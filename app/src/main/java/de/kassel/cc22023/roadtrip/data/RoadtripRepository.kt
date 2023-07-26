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

import de.kassel.cc22023.roadtrip.BuildConfig
import de.kassel.cc22023.roadtrip.data.local.database.PackingItem
import de.kassel.cc22023.roadtrip.data.local.database.PackingItemDao
import de.kassel.cc22023.roadtrip.data.local.database.CombinedLocation
import de.kassel.cc22023.roadtrip.data.local.database.CombinedRoadtrip
import de.kassel.cc22023.roadtrip.data.local.database.NotificationType
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripActivity
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripActivityDao
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripData
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripDataDao
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripLocation
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripLocationDao
import de.kassel.cc22023.roadtrip.data.local.database.STATIC_UID
import de.kassel.cc22023.roadtrip.data.network.OpenAiApi
import de.kassel.cc22023.roadtrip.data.network.model.RoadtripRequest
import de.kassel.cc22023.roadtrip.data.network.model.RoadtripRequestMessage
import de.kassel.cc22023.roadtrip.util.cleanJsonString
import de.kassel.cc22023.roadtrip.util.combineRoadtrip
import de.kassel.cc22023.roadtrip.util.convertCleanedStringToTrip
import de.kassel.cc22023.roadtrip.util.convertRoadtripFromTestTrip
import de.kassel.cc22023.roadtrip.util.createRoadtripPrompt
import de.kassel.cc22023.roadtrip.util.launch
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import java.io.FileInputStream
import java.util.Properties
import javax.inject.Inject

interface RoadtripRepository {
    val packingList: Flow<List<PackingItem>?>
    val roadtrip: Flow<RoadtripData?>
    val locations: Flow<List<RoadtripLocation>?>
    val activities: Flow<List<RoadtripActivity>?>

    suspend fun updateCheckbox(item: PackingItem)
    suspend fun insertIntoList(item: PackingItem)
    suspend fun insertNewRoadtrip(trip: CombinedRoadtrip)

    suspend fun createRoadtrip(
        startLocation: String,
        endLocation: String,
        startDate: String,
        endDate: String,
        transportation: String,
        onSuccess: (CombinedRoadtrip) -> Unit,
        onLoading: () -> Unit,
        onError: () -> Unit
    )

    fun getRoadtrip() : CombinedRoadtrip
    fun deleteItem(card: PackingItem)
}

class DefaultRoadtripRepository @Inject constructor(
    private val roadtripDataDao: RoadtripDataDao,
    private val packingItemDao: PackingItemDao,
    private val roadtripLocationDao: RoadtripLocationDao,
    private val roadtripActivityDao: RoadtripActivityDao,
    private val openAiApi: OpenAiApi
) : RoadtripRepository {
    override val packingList: Flow<List<PackingItem>?> =
        packingItemDao.getPackingItemsAsFlow()
    override val roadtrip: Flow<RoadtripData?>
        get() = roadtripDataDao.getRoadtripDataAsFlow()
    override val locations: Flow<List<RoadtripLocation>?>
        get() = roadtripLocationDao.getLocationsAsFlow()
    override val activities: Flow<List<RoadtripActivity>?>
        get() = roadtripActivityDao.getActivitiesAsFlow()

    override suspend fun insertNewRoadtrip(trip: CombinedRoadtrip) {
        roadtripDataDao.deleteRoadtrip()
        roadtripLocationDao.deleteAll()
        roadtripActivityDao.deleteAll()
        packingItemDao.deleteAllItems()

        roadtripDataDao.insertRoadtripData(RoadtripData(STATIC_UID, trip.startDate, trip.endDate, "Kassel", "not Kassel"))
        trip.locations.forEachIndexed {i, loc ->
            roadtripActivityDao.insertActivities(
                loc.activities.mapIndexed {i2, it ->
                    RoadtripActivity(i * 100 + i2, i, it.name)
                }
            )
        }
        roadtripLocationDao.insertLocations(
            trip.locations.mapIndexed {i, loc ->
                RoadtripLocation(i, loc.latitude, loc.longitude, loc.name)
            }
        )

        packingItemDao.insertPackingItems(
            trip.packingList.map {
                PackingItem(
                    0,
                    it,
                    NotificationType.NONE,
                    false
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
        onSuccess: (CombinedRoadtrip) -> Unit,
        onLoading: () -> Unit,
        onError: () -> Unit
    ) {
        val token = BuildConfig.GPT_KEY
        val contentType = "application/json"
        val prompt = createRoadtripPrompt(startLocation, endLocation, startDate, endDate, transportation)
        val request = RoadtripRequest(messages = listOf(RoadtripRequestMessage(content = prompt)))
        openAiApi.getRoadtrip( contentType, request)
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

    override fun getRoadtrip(): CombinedRoadtrip {
        val roadtrip = roadtripDataDao.getRoadtripData()
        val locations = roadtripLocationDao.getLocations()
        val activities = roadtripActivityDao.getActivities()
        val packingList = packingItemDao.getPackingItems()

        return combineRoadtrip(roadtrip, locations, activities, packingList)
    }

    override fun deleteItem(card: PackingItem) {
        packingItemDao.deleteItem(card)
    }

    override suspend fun updateCheckbox(item: PackingItem) {
        packingItemDao.updateCheckboxState(item)
    }



    override suspend fun insertIntoList(item: PackingItem) {
        packingItemDao.insertIntoList(item)
    }
}
