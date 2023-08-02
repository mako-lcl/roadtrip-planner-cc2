package de.kassel.cc22023.roadtrip.data.repository.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity()
data class RoadtripLocation(
    @PrimaryKey()
    val id: Int,
    val lat: Double?,
    val lon: Double?,
    val name: String?,
) {
    companion object {
        val exampleData = listOf(
            RoadtripLocation(
                0,
                50.0,
                9.0,
                "Burgburg"
            ),
            RoadtripLocation(
                0,
                51.0,
                9.0,
                "Not Kassel"
            ),
            RoadtripLocation(
                0,
                52.0,
                9.0,
                "City"
            ),
            RoadtripLocation(
                0,
                53.0,
                9.0,
                "Bergberg"
            ),
            RoadtripLocation(
                0,
                54.0,
                9.0,
                "Volcano"
            ),
            RoadtripLocation(
                0,
                55.0,
                9.0,
                "Bad Grissini"
            ),
        )
    }
}

@Dao
interface RoadtripLocationDao {
    @Query("SELECT * FROM RoadtripLocation")
    fun getLocations(): List<RoadtripLocation>

    @Query("SELECT * FROM RoadtripLocation")
    fun getLocationsAsFlow(): Flow<List<RoadtripLocation>>

    @Insert
    fun insertLocations(locations: List<RoadtripLocation>)

    @Query("DELETE FROM RoadtripLocation")
    fun deleteAll()
}