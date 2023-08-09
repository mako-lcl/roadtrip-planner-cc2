package de.kassel.cc22023.roadtrip.util

import android.content.Context
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import de.kassel.cc22023.roadtrip.data.network.model.OpenAiTrip
import de.kassel.cc22023.roadtrip.data.repository.database.NotificationType
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItem
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripActivity
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripAndLocationsAndList
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripData
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripLocation
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripLocationAndActivity
import timber.log.Timber
import java.io.IOException

fun convertRoadtripFromTestTrip(trip: OpenAiTrip) : RoadtripAndLocationsAndList {
    val locations = trip.locs.map {
        val activities = it.activities.map {
            RoadtripActivity(0, 0, it)
        }

        val location = RoadtripLocation(0, 0, it.latitude, it.longitude, it.name)
        RoadtripLocationAndActivity(location, activities)
    }

    val roadtrip = RoadtripData(0, trip.startDate, trip.endDate, trip.startLocation, trip.endLocation)

    val packingList = trip.packingList.map {
        PackingItem(0, 0, it, NotificationType.NONE, false, 0, 0.0,0.0,0.0)
    }

    val roadtripCombined = RoadtripAndLocationsAndList(
        roadtrip, locations, packingList
    )

    return roadtripCombined
}

fun loadRoadtripFromAssets(context: Context) : OpenAiTrip {
    val string = context.assets.open("roadtrip.txt").bufferedReader().use{
        it.readText()
    }

    val moshi: Moshi = Moshi.Builder().build()
    val jsonAdapter: JsonAdapter<OpenAiTrip> = moshi.adapter(OpenAiTrip::class.java)
    val trip = jsonAdapter.fromJson(string)

    trip?.let {
        return it
    }

    throw IOException()
}

fun convertCleanedStringToTrip(json: String) : OpenAiTrip? {
    Timber.d(json)
    val moshi: Moshi = Moshi.Builder().build()
    val jsonAdapter: JsonAdapter<OpenAiTrip> = moshi.adapter(OpenAiTrip::class.java)
    try {
        val trip = jsonAdapter.fromJson(json)
        trip?.let {
            return trip
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}