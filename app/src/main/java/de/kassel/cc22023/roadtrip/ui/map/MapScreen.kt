package de.kassel.cc22023.roadtrip.ui.map

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripLocation
import de.kassel.cc22023.roadtrip.ui.util.FetchLocationUtil
import de.kassel.cc22023.roadtrip.ui.util.LoadingScreen
import de.kassel.cc22023.roadtrip.util.PermissionBox

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

    //fetch the location
    FetchLocationUtil(usePreciseLocation = usePreciseLocation)

    if (location != null) {
        val currentLatLng = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(currentLatLng, 10f)
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            RoadtripLocation.exampleData.forEach {loc ->
                Marker(
                    state = rememberMarkerState(position = LatLng(loc.lat ?: 0.0, loc.lon ?: 0.0)),
                    title = loc.name ?: "Unknown",
                    snippet = "Bunch of activities to do!",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                )
            }

            val markerCoords = RoadtripLocation.exampleData.map {
                LatLng(it.lat ?: 0.0, it.lon ?: 0.0)
            }

            Polyline(
                points = markerCoords,
                color = Color.Magenta
            )
        }
    } else {
        LoadingScreen()
    }
}
