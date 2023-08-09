package de.kassel.cc22023.roadtrip.ui.map

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kassel.cc22023.roadtrip.data.preferences.PreferenceStore
import de.kassel.cc22023.roadtrip.data.repository.RoadtripRepository
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripAndLocationsAndList
import de.kassel.cc22023.roadtrip.data.sensors.SensorRepository
import de.kassel.cc22023.roadtrip.ui.planner.PlannerDataUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    roadtripRepository: RoadtripRepository,
    private val sensorRepository: SensorRepository,
    preferences: PreferenceStore
) : ViewModel() {
    val location: StateFlow<Location?> = sensorRepository.locationFlow

    val data: StateFlow<MapDataUiState> = combine(
        roadtripRepository.allRoadtrips,
        preferences.currentTrip
    ) { trips, currentTripIndex ->
        if (trips != null) {
            val currentTrip = trips.firstOrNull { it.trip.id == currentTripIndex }
            if (currentTrip != null) {
                MapDataUiState.Success(currentTrip)
            } else {
                MapDataUiState.NoTrip
            }
        } else {
            MapDataUiState.Loading
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MapDataUiState.Loading
        )

    fun locationPermissionGranted() {
        sensorRepository.permissionsGranted()
    }
}

sealed interface MapDataUiState {
    object Loading : MapDataUiState
    data class Success(val data: RoadtripAndLocationsAndList) : MapDataUiState

    object NoTrip : MapDataUiState
}