package de.kassel.cc22023.roadtrip.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "RoadtripActivity",
    foreignKeys = [
        ForeignKey(
            entity = RoadtripLocation::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoadtripActivity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val locationId: Int,
    val name: String?
) {
    companion object {
        val exampleData = listOf(
            RoadtripActivity(0,  0,"Hiking"),
            RoadtripActivity(0, 0, "Eating"),
            RoadtripActivity(0, 0, "Sleeping"),
            RoadtripActivity(0, 0, "Catching Fairies"),
            RoadtripActivity(0, 0, "Programming"),
            RoadtripActivity(0, 0, "Code Camp 2"),
            RoadtripActivity(0, 0, "Learning"),
            RoadtripActivity(0, 0, "Singing"),
            RoadtripActivity(0, 0, "Swimming"),
        )
    }
}