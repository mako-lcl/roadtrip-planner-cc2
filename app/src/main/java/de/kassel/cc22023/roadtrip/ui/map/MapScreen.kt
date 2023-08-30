package de.kassel.cc22023.roadtrip.ui.map

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
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
            MapLoadingScreen()
        },
    )
}

@RequiresPermission(
    anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
)
@Composable
fun MapLoadingScreen(
    viewModel: MapViewModel = hiltViewModel()
) {
    val location by viewModel.location.collectAsState()
    val data by viewModel.data.collectAsState()

    viewModel.locationPermissionGranted()

    if (location != null) {
        when (data) {
            is MapDataUiState.Success -> {
                TripMapView()
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
