package de.kassel.cc22023.roadtrip.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

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
    val id: Int,
    val name: String,
    var notificationType: NotificationType,
    var isChecked: Boolean,
    var time: Long?,
    val height: Float,
    val lat: Float,
    val lon: Float
) {
    companion object {
        val exampleData = listOf(
            PackingItem(
                0,
                "Driver's License",
                NotificationType.NONE,
                isChecked = false,
                null,
                0f,
                0f,
                0f

            ),
            PackingItem(
                1,
                "Bread",
                NotificationType.FLOOR,
                isChecked = false,
                null,
                0f,
                0f,
                0f
            ),
            PackingItem(
                2,
                "Ham",
                NotificationType.NONE,
                isChecked = false,
                null,
                0f,
                0f,
                0f
            ),
            PackingItem(
                3,
                "Coke Zero",
                NotificationType.NONE,
                isChecked = false,
                null,
                0f,
                0f,
                0f
            ),
            PackingItem(
                4,
                "Yoshi Plush",
                NotificationType.NONE,
                isChecked = false,
                null,
                0f,
                0f,
                0f
            ),
            PackingItem(
                5,
                "Grissini",
                NotificationType.NONE,
                isChecked = false,
                null,
                0f,
                0f,
                0f
            ),
            PackingItem(
                6,
                "iPad",
                NotificationType.NONE,
                isChecked = false,
                null,
                0f,
                0f,
                0f
            ),
        )
    }
}

@Dao
interface PackingItemDao {
    @Query("SELECT * FROM PackingItem")
    fun getPackingItems(): List<PackingItem>

    @Query("SELECT * FROM PackingItem")
    fun getPackingItemsAsFlow(): Flow<List<PackingItem>>

    @Insert
    suspend fun insertPackingItems(items: List<PackingItem>)

    @Query("DELETE FROM PackingItem")
    fun deleteAllItems()

    @Delete
    fun deleteItem(item: PackingItem)

    @Insert
    fun insertIntoList(item: PackingItem)

    @Update
    fun updateItem(item: PackingItem)

    @Insert
    suspend fun insertPackingItem(item: PackingItem)
}