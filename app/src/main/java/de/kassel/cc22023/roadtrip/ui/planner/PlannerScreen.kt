package de.kassel.cc22023.roadtrip.ui.planner

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson

@Composable
fun PlannerScreen() {
    val context = LocalContext.current

    Text(text = "Planner")

    val string = context.assets.open("roadtrip.txt").bufferedReader().use{
        it.readText()
    }

    val moshi: Moshi = Moshi.Builder().build()
    val jsonAdapter: JsonAdapter<TestTrip> = moshi.adapter(TestTrip::class.java)
    val trip = jsonAdapter.fromJson(string)

    print(trip)
}

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