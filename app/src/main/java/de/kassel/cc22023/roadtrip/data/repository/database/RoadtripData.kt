package de.kassel.cc22023.roadtrip.data.repository.database

import androidx.room.Dao
import androidx.room.Delete
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
    @PrimaryKey(autoGenerate = true)
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
    @Delete
    fun deleteRoadtrip(trip: RoadtripData)
    @Query("DELETE FROM RoadtripData")
    fun nukeDB()
}
