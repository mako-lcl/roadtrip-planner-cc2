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

import de.kassel.cc22023.roadtrip.data.local.database.CombinedLocation
import de.kassel.cc22023.roadtrip.data.local.database.CombinedRoadtrip
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripActivity
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripActivityDao
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripData
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripDataDao
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripLocation
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripLocationDao
import de.kassel.cc22023.roadtrip.data.local.database.STATIC_UID
import de.kassel.cc22023.roadtrip.data.network.OpenAiApi
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface RoadtripRepository {
    suspend fun insertNewRoadtrip(trip: CombinedRoadtrip)
    fun getRoadtrip() : CombinedRoadtrip
}

class DefaultWeatherDataRepository @Inject constructor(
    private val roadtripDataDao: RoadtripDataDao,
    private val roadtripLocationDao: RoadtripLocationDao,
    private val roadtripActivityDao: RoadtripActivityDao,
    private val weatherApi: OpenAiApi
) : RoadtripRepository {
    override suspend fun insertNewRoadtrip(trip: CombinedRoadtrip) {
        roadtripDataDao.deleteRoadtrip()
        roadtripLocationDao.deleteAll()
        roadtripActivityDao.deleteAll()

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
    }

    override fun getRoadtrip(): CombinedRoadtrip {
        val roadtrip = roadtripDataDao.getRoadtripData()
        val locations = roadtripLocationDao.getLocations()
        val activities = roadtripActivityDao.getActivities()

        val combinedLocations = locations.map {
            CombinedLocation(
                it.lat ?: 0.0, it.lon ?: 0.0, it.name ?: "",
                activities.filter {activity ->
                    it.id == activity.locId
                }
            )
        }

        val combinedRoadtrip = CombinedRoadtrip(roadtrip.startDate ?: "", roadtrip.endDate ?: "", "", "", listOf("abc"), combinedLocations)

        return combinedRoadtrip
    }
}
