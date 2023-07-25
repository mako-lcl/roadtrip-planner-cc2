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
            RoadtripActivity(1, 0, "Eating"),
            RoadtripActivity(2, 0, "Sleeping"),
            RoadtripActivity(3, 0, "Catching Fairies"),
            RoadtripActivity(4, 0, "Programming"),
            RoadtripActivity(5, 0, "Code Camp 2"),
            RoadtripActivity(6, 0, "Learning"),
            RoadtripActivity(7, 0, "Singing"),
            RoadtripActivity(8, 0, "Swimming"),
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