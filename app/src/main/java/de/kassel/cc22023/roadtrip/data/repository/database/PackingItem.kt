package de.kassel.cc22023.roadtrip.data.repository.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

enum class NotificationType(val value: String) {
    NONE("None"),
    FLOOR("Floor"),
    LOCATION("Location"),
    TIME("Time");

    companion object {
        fun fromString(type: String) : NotificationType {
            return when (type) {
                "None" -> {
                    NONE
                }
                "Floor" -> {
                    FLOOR
                }
                "Location" -> {
                    LOCATION
                }
                else -> {
                    TIME
                }
            }
        }
    }
}

@Entity
data class PackingItem(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    val tripId: Long,
    var name: String,
    var notificationType: NotificationType,
    var isChecked: Boolean,
    var time: Long?,
    var floor: Int,
    var lat: Double,
    var lon: Double,
    var image: String? = null
) {


    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + tripId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + notificationType.hashCode()
        result = 31 * result + isChecked.hashCode()
        result = 31 * result + (time?.hashCode() ?: 0)
        result = 31 * result + floor.hashCode()
        result = 31 * result + lat.hashCode()
        result = 31 * result + lon.hashCode()
        result = 31 * result + image.hashCode()
        return result
    }
}



@Dao
interface PackingItemDao {
    @Query("SELECT * FROM PackingItem")
    fun getPackingItems(): List<PackingItem>

    @Query("SELECT * FROM PackingItem")
    fun getPackingItemsAsFlow(): Flow<List<PackingItem>>

    @Insert
    fun insertPackingItems(items: List<PackingItem>) : List<Long>

    @Query("DELETE FROM PackingItem")
    fun deleteAllItems()

    @Delete
    fun deleteItem(item: PackingItem)

    @Insert
    fun insertIntoList(item: PackingItem) : Long

    @Update
    fun updateItem(item: PackingItem)

    @Update
    fun updateItems(items: List<PackingItem>)

    @Insert
    suspend fun insertPackingItem(item: PackingItem)

    @Query("DELETE FROM PackingItem")
    fun nukeDB()
}