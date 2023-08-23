package de.kassel.cc22023.roadtrip.data.repository.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import kotlinx.coroutines.flow.Flow

@Entity
data class RoadtripLocation(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val tripId: Long,
    val lat: Double?,
    val lon: Double?,
    val name: String?,
    val mustHave:Boolean,
    val date: String
)

data class RoadtripLocationAndActivity(
    @Embedded val location: RoadtripLocation,
    @Relation(
        entity = RoadtripActivity::class,
        parentColumn = "id",
        entityColumn = "locationId"
    )
    val activities: List<RoadtripActivity>
)

@Dao
interface RoadtripLocationDao {
    @Insert
    fun insertLocations(locations: List<RoadtripLocation>) : List<Long>

    @Query("DELETE FROM RoadtripLocation")
    fun deleteAll()

    @Query("DELETE FROM RoadtripLocation")
    fun nukeDB()

    @Delete
    fun deleteLocation(location: RoadtripLocation)
}