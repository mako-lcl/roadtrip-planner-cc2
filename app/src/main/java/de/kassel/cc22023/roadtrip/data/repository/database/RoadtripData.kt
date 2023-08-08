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

package de.kassel.cc22023.roadtrip.data.repository.database

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.squareup.moshi.Json
import kotlinx.coroutines.flow.Flow

enum class TransportationType(val value: String) {
    CAR("by car"),
    BIKE("by bike"),
    BICYCLE("by bicycle"),
    HIKING("on foot")
}

@Entity
data class RoadtripData(
    @PrimaryKey
    val id: Long = 0,
    @Json(name = "start_date")
    val startDate: String?,
    @Json(name = "end_date")
    val endDate: String?,
    val startLocation: String?,
    val endLocation: String?,
)

data class RoadtripAndLocationsAndList(
    @Embedded val trip: RoadtripData,
    @Relation(
        entity = RoadtripLocation::class,
        parentColumn = "id",
        entityColumn = "tripId"
    )
    val locations: List<RoadtripLocationAndActivity>,
    @Relation(
        entity = PackingItem::class,
        parentColumn = "id",
        entityColumn = "tripId"
    )
    val packingItems: List<PackingItem>
)

@Dao
interface RoadtripDataDao {
    @Transaction
    @Query("SELECT * FROM RoadtripData WHERE roadtripdata.id == 0")
    fun getRoadtripAndLocations(): RoadtripAndLocationsAndList

    @Transaction
    @Query("SELECT * FROM RoadtripData WHERE roadtripdata.id == 0")
    fun getRoadtripAndLocationsAsFlow(): Flow<RoadtripAndLocationsAndList>

    @Transaction
    @Query("SELECT * FROM RoadtripData")
    fun getAllRoadtripsAsFlow(): Flow<List<RoadtripAndLocationsAndList>>

    @Insert
    fun insertRoadtripData(item: RoadtripData) : Long
    @Query("DELETE FROM roadtripdata WHERE roadtripdata.id = 0")
    suspend fun deleteRoadtrip()
}
