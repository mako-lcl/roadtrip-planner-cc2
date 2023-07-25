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

package de.kassel.cc22023.roadtrip.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.squareup.moshi.Json
import de.kassel.cc22023.roadtrip.ui.planner.Loc
import kotlinx.coroutines.flow.Flow

const val STATIC_UID = 0
enum class TransportationType(val value: String) {
    CAR("Car"),
    BIKE("Bike"),
    BICYCLE("Bicycle"),
    HIKING("Hiking")
}
@Entity
data class RoadtripData(
    @PrimaryKey
    val uid: Int = STATIC_UID,
    @Json(name = "start_date")
    val startDate: String?,
    @Json(name = "end_date")
    val endDate: String?,
    val startLocation: String?,
    val endLocation: String?,
) {
    companion object {
        val exampleData = RoadtripData(
            STATIC_UID,
            "today",
            "tomorrow",
            "Kassel",
            "Not Kassel",
        )
    }
}

data class RoadtripAndLocationsAndActivities(
    @Embedded val roadtrip: RoadtripData,
    @Relation(
        parentColumn = "uid",
        entityColumn = "roadtripId"
    )
    val locations: List<RoadtripLocation>
)


@Dao
interface RoadtripDataDao {
    @Transaction
    @Query("SELECT * FROM roadtripdata WHERE roadtripdata.uid == $STATIC_UID")
    fun getRoadtripData(): Flow<RoadtripAndLocationsAndActivities>

    @Insert
    suspend fun insertRoadtripData(item: RoadtripData)

    @Query("DELETE FROM roadtripdata WHERE roadtripdata.uid = $STATIC_UID")
    fun deleteRoadtrip()
}
