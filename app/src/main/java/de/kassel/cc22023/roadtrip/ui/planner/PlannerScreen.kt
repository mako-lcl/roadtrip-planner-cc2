package de.kassel.cc22023.roadtrip.ui.planner

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import de.kassel.cc22023.roadtrip.util.convertRoadtripFromTestTrip
import de.kassel.cc22023.roadtrip.util.loadRoadtripFromAssets

@Composable
fun PlannerScreen(
    viewModel: PlannerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val trip by viewModel.trip.collectAsState()

    Column {
        Text(text = "Planner")
        Text(trip.toString())

        Button(onClick = {
            val testTrip = loadRoadtripFromAssets(context)
            val trip = convertRoadtripFromTestTrip(testTrip)
            viewModel.insertNewRoadtrip(trip)
        }) {
            Text("Load data!")
        }

        Button(onClick = {
            viewModel.getRoadtrip()
        }) {
            Text("Show")
        }
    }
}