package de.kassel.cc22023.roadtrip.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import de.kassel.cc22023.roadtrip.util.makeActivityList
import okhttp3.internal.toImmutableList
import timber.log.Timber

const val START_HUE = BitmapDescriptorFactory.HUE_AZURE
const val END_HUE = BitmapDescriptorFactory.HUE_AZURE
const val MUST_HAVE_HUE = BitmapDescriptorFactory.HUE_RED
const val NORMAL_HUE = BitmapDescriptorFactory.HUE_ORANGE

@Composable
fun TripMapView(
    viewModel: MapViewModel = hiltViewModel()
) {
    val data by viewModel.data.collectAsState()

    if (data is MapDataUiState.Success) {
        val currentLatLng = LatLng(
            (data as MapDataUiState.Success).data.locations.firstOrNull()?.location?.lat ?: 0.0,
            (data as MapDataUiState.Success).data.locations.firstOrNull()?.location?.lon ?: 0.0
        )

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(currentLatLng, 7f)
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Timber.d("locations: ${(data as MapDataUiState.Success).data.locations.count()}")
            val locations = (data as MapDataUiState.Success).data.locations.toImmutableList()
            locations.forEachIndexed { i, loc ->
                val activities = loc.activities.makeActivityList()

                val markerState = rememberMarkerState(
                    position = LatLng(
                        loc.location.lat ?: 0.0,
                        loc.location.lon ?: 0.0
                    )
                )
                val hue = when (i) {
                    0 -> {
                        START_HUE
                    }

                    locations.size - 1 -> {
                        END_HUE
                    }

                    else -> {
                        if (loc.location.mustHave) {
                            MUST_HAVE_HUE
                        } else {
                            NORMAL_HUE
                        }
                    }
                }
                markerState.position = LatLng(loc.location.lat ?: 0.0, loc.location.lon ?: 0.0)
                MarkerInfoWindow(
                    state = markerState,
                    title = loc.location.name ?: "Unknown",
                    icon = BitmapDescriptorFactory.defaultMarker(hue),

                    ) {
                    Box(
                        //contentAlignment = Alignment.TopEnd,

                    ) {

                        Card(


                        ) {
                            Column(
                                modifier = Modifier.background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFFF4E0B9),
                                            Color(0xFFDFA878)
                                        )
                                    )
                                ).padding(all = 16.dp),


                                verticalArrangement = Arrangement.spacedBy(16.dp),

                                ) {

                                Text(
                                    loc.location.name ?: "Unknown",
                                    fontSize = 20.sp,
                                    color = Color(0xFFBA704F),
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.Serif
                                )

                                Text(
                                    "Date: ${loc.location.date}",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Normal)

                                Text(
                                    "Activities:\n$activities",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            val markerCoords = (data as MapDataUiState.Success).data.locations.map {
                LatLng(it.location.lat ?: 0.0, it.location.lon ?: 0.0)
            }

            Polyline(
                points = markerCoords,
                color = Color.Magenta
            )
        }
    } else {
        CircularProgressIndicator()
    }
}