package de.kassel.cc22023.roadtrip.data.local.database

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
    BASEMENT("Basement")
}

@Entity
data class PackingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val notificationType: NotificationType
) {
    companion object {
        val exampleData = listOf(
            PackingItem(
                0,
                "Driver's License",
                NotificationType.NONE
            ),
            PackingItem(
                0,
                "Bread",
                NotificationType.BASEMENT
            ),
            PackingItem(
                0,
                "Ham",
                NotificationType.NONE
            ),
            PackingItem(
                0,
                "Coke Zero",
                NotificationType.NONE
            ),
            PackingItem(
                0,
                "Yoshi Plush",
                NotificationType.NONE
            ),
            PackingItem(
                0,
                "Grissini",
                NotificationType.NONE
            ),
            PackingItem(
                0,
                "iPad",
                NotificationType.NONE
            ),
        )
    }
}

@Dao
interface PackingItemDao {
    @Query("SELECT * FROM PackingItem")
    fun getPackingItems(): Flow<List<PackingItem>>

    @Insert
    suspend fun insertPackingItems(items: List<PackingItem>)

    @Query("DELETE FROM PackingItem")
    fun deleteAllItems()

    @Update
    fun updateItem(item: PackingItem)
}