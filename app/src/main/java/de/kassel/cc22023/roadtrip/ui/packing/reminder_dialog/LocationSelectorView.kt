package de.kassel.cc22023.roadtrip.ui.packing.reminder_dialog

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import de.kassel.cc22023.roadtrip.ui.packing.PackingViewModel

@Composable
fun LocationSelectorView(
    selectLocation: (LatLng) -> Unit,
    viewModel: PackingViewModel = hiltViewModel()
) {
    val location by viewModel.location.collectAsState()

    if (location != null) {
        val latLng = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(latLng, 10f)
        }

        var selectedLatLng by remember { mutableStateOf(latLng) }

        val markerState = rememberMarkerState(position = selectedLatLng)

        GoogleMap(
            cameraPositionState = cameraPositionState,
            onMapClick = {
                selectedLatLng = it
                markerState.position = selectedLatLng
                selectLocation(it)
            }
        ) {
            MarkerInfoWindow(
                state = markerState,
            )
        }
    } else {
        CircularProgressIndicator()
    }
}