package de.kassel.cc22023.roadtrip.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "RoadtripLocation",
    foreignKeys = [
        ForeignKey(
            entity = RoadtripData::class,
            parentColumns = ["uid"],
            childColumns = ["roadtripId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoadtripLocation(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val roadtripId: Int,
    val lat: Double?,
    val lon: Double?,
    val name: String?
) {
    companion object {
        val exampleData = listOf(
            RoadtripLocation(
                0,
                0,
                50.0,
                9.0,
                "Burgburg"
            ),
            RoadtripLocation(
                0,
                0,
                51.0,
                9.0,
                "Not Kassel"
            ),
            RoadtripLocation(
                0,
                0,
                52.0,
                9.0,
                "City"
            ),
            RoadtripLocation(
                0,
                0,
                53.0,
                9.0,
                "Bergberg"
            ),
            RoadtripLocation(
                0,
                0,
                54.0,
                9.0,
                "Volcano"
            ),
            RoadtripLocation(
                0,
                0,
                55.0,
                9.0,
                "Bad Grissini"
            ),
        )
    }
}

data class LocationsAndActivities(
    @Embedded val location: RoadtripLocation,
    @Relation(
        parentColumn = "id",
        entityColumn = "roadtripId"
    )
    val activities: List<RoadtripActivity>
)