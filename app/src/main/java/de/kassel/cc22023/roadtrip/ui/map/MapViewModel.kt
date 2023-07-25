package de.kassel.cc22023.roadtrip.ui.map

import android.location.Location
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kassel.cc22023.roadtrip.data.RoadtripRepository
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val roadtripRepository: RoadtripRepository
) : ViewModel() {
    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location

    fun updateLocation(newLocation: Location?) {
        newLocation?.let {
            _location.value = newLocation
        }
    }

    sealed interface RoadtripUiState {
        object Loading : RoadtripUiState
        data class Success(val data: RoadtripData) : RoadtripUiState
    }
}
