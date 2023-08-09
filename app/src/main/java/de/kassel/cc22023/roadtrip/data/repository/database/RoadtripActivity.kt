package de.kassel.cc22023.roadtrip.data.repository.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity
data class RoadtripActivity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val locationId: Long,
    val name: String?
)

@Dao
interface RoadtripActivityDao {
    @Insert
    fun insertActivities(activities: List<RoadtripActivity>)

    @Query("DELETE FROM RoadtripActivity")
    fun deleteAll()

    @Query("DELETE FROM RoadtripActivity")
    fun nukeDB()

    @Delete
    fun deleteActivity(activity: RoadtripActivity)
}