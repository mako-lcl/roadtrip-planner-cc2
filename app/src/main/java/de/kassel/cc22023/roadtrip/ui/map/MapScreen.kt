package de.kassel.cc22023.roadtrip.ui.map

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopEnd
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
import de.kassel.cc22023.roadtrip.ui.util.LoadingScreen
import de.kassel.cc22023.roadtrip.util.PermissionBox
import de.kassel.cc22023.roadtrip.util.makeActivityList
import okhttp3.internal.toImmutableList
import timber.log.Timber

@RequiresPermission(
    anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
)
@Composable
fun MapScreen() {
    val permissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    PermissionBox(
        permissions = permissions,
        requiredPermissions = listOf(permissions.first()),
        description = "App needs location data",
        onGranted = {
            MapLoadingScreen(usePreciseLocation = it.contains(Manifest.permission.ACCESS_FINE_LOCATION))
        },
    )
}

@RequiresPermission(
    anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
)
@Composable
fun MapLoadingScreen(
    usePreciseLocation: Boolean,

    viewModel: MapViewModel = hiltViewModel()
) {
    val location by viewModel.location.collectAsState()
    val data by viewModel.data.collectAsState()

    viewModel.locationPermissionGranted()

    if (location != null) {
        when (data) {
            is MapDataUiState.Success -> {
                val currentLatLng = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)

                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(currentLatLng, 10f)
                }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    Timber.d("locations: ${(data as MapDataUiState.Success).data.locations.count()}")
                    val locations = (data as MapDataUiState.Success).data.locations.toImmutableList()
                    locations.forEach {loc ->
                        val activities = loc.activities.makeActivityList()
                        MarkerInfoWindow(
                            state = rememberMarkerState(position = LatLng(loc.latitude, loc.longitude)),
                            title = loc.name,
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                        ) {
                            Box( contentAlignment = TopEnd,
                            modifier = Modifier.wrapContentSize(TopEnd)) {

                        Card(
                                modifier = Modifier
                                    .background(Color.Transparent)
                                    .width(250.dp),
                                shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp, bottomStart = 0.dp, bottomEnd = 25.dp)
                            ) {
                                Column(
                                    horizontalAlignment = CenterHorizontally,
                                    modifier = Modifier.padding(0.dp)
                                        .background(Brush.linearGradient(listOf( Color(0xFFF4E0B9),Color(0xFFDFA878)))),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),

                                ) {
                                    Text(loc.name, fontSize = 20.sp, color = Color(0xFFBA704F), fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Serif)
                                    Text("Activities:\n$activities", color = Color.Black, fontWeight = FontWeight.Normal)
                                }
                            }
                            }
                        }
                    }

                    val markerCoords = (data as MapDataUiState.Success).data.locations.map {
                        LatLng(it.latitude, it.longitude)
                    }

                    Polyline(
                        points = markerCoords,
                        color = Color.Magenta
                    )
                }
            }

            MapDataUiState.Loading -> {
                LoadingScreen()
            }

            MapDataUiState.NoTrip -> {
                NoTripScreen()
            }
        }
    } else {
        LoadingScreen()
    }
}
