package de.kassel.cc22023.roadtrip.ui.map

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripLocation
import de.kassel.cc22023.roadtrip.ui.util.FetchLocationUtil
import de.kassel.cc22023.roadtrip.ui.util.LoadingScreen
import de.kassel.cc22023.roadtrip.util.PermissionBox
import de.kassel.cc22023.roadtrip.util.makeActivityList

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

    //fetch the location
    FetchLocationUtil(usePreciseLocation = usePreciseLocation)

    if (location != null) {
        if (data is MapDataUiState.Success) {
            val currentLatLng = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(currentLatLng, 10f)
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                (data as MapDataUiState.Success).data.locations.forEach {loc ->
                    val activities = loc.activities.makeActivityList()
                    MarkerInfoWindow(
                        state = rememberMarkerState(position = LatLng(loc.latitude, loc.longitude)),
                        title = loc.name,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                    ) {
                        Card(
                            modifier = Modifier
                                .background(Color.White)
                                .width(250.dp)
                        ) {
                            Column(
                                horizontalAlignment = CenterHorizontally,
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(loc.name, fontSize = 20.sp)
                                Text("Activities:\n$activities")
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
        } else if (data == MapDataUiState.Loading) {
            LoadingScreen()
        } else if (data == MapDataUiState.NoTrip) {
            Text("No trip yet!")
        }
    } else {
        LoadingScreen()
    }
}
