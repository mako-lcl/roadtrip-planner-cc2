package de.kassel.cc22023.roadtrip.util

import android.content.Context
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import de.kassel.cc22023.roadtrip.data.local.database.CombinedLocation
import de.kassel.cc22023.roadtrip.data.local.database.CombinedRoadtrip
import de.kassel.cc22023.roadtrip.data.local.database.NotificationType
import de.kassel.cc22023.roadtrip.data.local.database.PackingItem
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripActivity
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripData
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripLocation
import de.kassel.cc22023.roadtrip.data.local.database.STATIC_UID
import java.io.IOException

@JsonClass(generateAdapter = true)
data class TestTrip(
    @Json(name = "start_date")
    val startDate: String,
    @Json(name = "end_date")
    val endDate: String,
    @Json(name = "packing_list")
    val packingList: List<String>,
    @Json(name = "locations")
    val locs: List<Loc>
)

@JsonClass(generateAdapter = true)
data class Loc(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val activities: List<String>
)

fun combineRoadtrip(data: RoadtripData, locations: List<RoadtripLocation>, activities: List<RoadtripActivity>, packingList: List<PackingItem>) : CombinedRoadtrip {
    val combinedLocations = locations.map {
        CombinedLocation(
            it.lat ?: 0.0, it.lon ?: 0.0, it.name ?: "",
            activities.filter {activity ->
                it.id == activity.locId
            }
        )
    }

    val combinedRoadtrip = CombinedRoadtrip(data.startDate ?: "", data.endDate ?: "", "", "", packingList.map { it.name }, combinedLocations)

    return combinedRoadtrip
}

fun convertRoadtripFromTestTrip(trip: TestTrip) : CombinedRoadtrip {
    val locations = trip.locs.map {
        val activities = it.activities.map {
            RoadtripActivity(0, 0, it)
        }

        CombinedLocation(it.latitude, it.longitude, it.name, activities)
    }

    val roadtripCombined = CombinedRoadtrip(
        trip.startDate, trip.endDate, "", "", trip.packingList,
        locations
    )

    return roadtripCombined
}

fun loadRoadtripFromAssets(context: Context) : TestTrip {
    val string = context.assets.open("roadtrip.txt").bufferedReader().use{
        it.readText()
    }

    val moshi: Moshi = Moshi.Builder().build()
    val jsonAdapter: JsonAdapter<TestTrip> = moshi.adapter(TestTrip::class.java)
    val trip = jsonAdapter.fromJson(string)

    trip?.let {
        return it
    }

    throw IOException()
}