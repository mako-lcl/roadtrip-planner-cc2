package de.kassel.cc22023.roadtrip.ui.map

import android.location.Location
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kassel.cc22023.roadtrip.data.RoadtripRepository
import de.kassel.cc22023.roadtrip.data.local.database.CombinedRoadtrip
import de.kassel.cc22023.roadtrip.data.local.database.PackingItem
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripData
import de.kassel.cc22023.roadtrip.ui.packing.PackingDataUiState
import de.kassel.cc22023.roadtrip.util.combineRoadtrip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val roadtripRepository: RoadtripRepository
) : ViewModel() {
    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location

    val data: StateFlow<MapDataUiState> = combine(
        roadtripRepository.roadtrip,
        roadtripRepository.locations,
        roadtripRepository.activities,
        roadtripRepository.packingList
    ) { trip, locations, activities, packingList ->
        if (trip != null && locations != null && activities != null && packingList != null) {
            MapDataUiState.Success(
                combineRoadtrip(trip, locations, activities, packingList)
            )
        } else {
            MapDataUiState.Loading
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MapDataUiState.Loading
        )

    fun updateLocation(newLocation: Location?) {
        newLocation?.let {
            _location.value = newLocation
        }
    }
}

sealed interface MapDataUiState {
    object Loading : MapDataUiState
    data class Success(val data: CombinedRoadtrip) : MapDataUiState
}