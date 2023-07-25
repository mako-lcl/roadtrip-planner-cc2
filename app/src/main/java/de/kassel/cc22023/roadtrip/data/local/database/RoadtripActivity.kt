package de.kassel.cc22023.roadtrip.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity()
data class RoadtripActivity(
    @PrimaryKey()
    val id: Int,
    val locId: Int,
    val name: String?
) {
    companion object {
        val exampleData = listOf(
            RoadtripActivity(0, 0,  "Hiking"),
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

@Dao
interface RoadtripActivityDao {
    @Query("SELECT * FROM RoadtripActivity")
    fun getActivities(): List<RoadtripActivity>

    @Query("SELECT * FROM RoadtripActivity")
    fun getActivitiesAsFlow(): Flow<List<RoadtripActivity>>

    @Insert
    fun insertActivities(activities: List<RoadtripActivity>)

    @Query("DELETE FROM RoadtripActivity")
    fun deleteAll()
}