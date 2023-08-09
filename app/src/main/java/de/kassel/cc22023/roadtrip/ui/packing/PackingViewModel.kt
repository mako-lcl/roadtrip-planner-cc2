package de.kassel.cc22023.roadtrip.ui.packing
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kassel.cc22023.roadtrip.data.repository.RoadtripRepository
import de.kassel.cc22023.roadtrip.data.repository.database.NotificationType
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItem
import de.kassel.cc22023.roadtrip.data.preferences.PreferenceStore
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripAndLocationsAndList
import de.kassel.cc22023.roadtrip.data.sensors.SensorRepository
import de.kassel.cc22023.roadtrip.geofence.GeofenceManager
import de.kassel.cc22023.roadtrip.ui.map.MapDataUiState
import de.kassel.cc22023.roadtrip.ui.planner.PlannerDataUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PackingViewModel @Inject constructor(
    private val roadtripRepository: RoadtripRepository,
    private val sensorRepository: SensorRepository,
    private val geofenceManager: GeofenceManager,
    private val preferences: PreferenceStore,
) : ViewModel() {
    val height = preferences.height
    val location: StateFlow<Location?> = sensorRepository.locationFlow

    val data: StateFlow<PackingDataUiState> = combine(
        roadtripRepository.allRoadtrips,
        preferences.currentTrip
    ) { trips, currentTripIndex ->
        if (trips != null) {
            val currentTrip = trips.firstOrNull { it.trip.id == currentTripIndex }
            if (currentTrip != null) {
                PackingDataUiState.Success(currentTrip)
            } else {
                PackingDataUiState.NoList
            }
        } else {
            PackingDataUiState.Loading
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PackingDataUiState.Loading
        )

    private fun refreshGeofences(items: List<PackingItem>) {
        if (items.isEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                geofenceManager.deregisterGeofence()
            }
            return
        }

        geofenceManager.clearGeofences()

        items.forEach {item ->
            addGeofence(item.id, location = Location("").apply {
                latitude = item.lat
                longitude = item.lon
            })
            Timber.d("added fence for ${item.name}, loc: ${item.lat}, ${item.lon}")
        }

        viewModelScope.launch(Dispatchers.IO) {
            geofenceManager.deregisterGeofenceAndReregisterFences()
        }
    }

    private fun addGeofence(key: Long, location: Location) {
        geofenceManager.addGeofence(key, location)
    }

    fun locationPermissionGranted() {
        sensorRepository.permissionsGranted()

        startListeningGeofences()
    }

    var startedListeninggGeofences = false

    private fun startListeningGeofences() {
        if (!startedListeninggGeofences) {
            startedListeninggGeofences = true
            viewModelScope.launch(Dispatchers.IO) {
                data.collect {data ->
                    if (data is PackingDataUiState.Success) {
                        val items = data.data.packingItems.filter {
                            //only geofence FLOOR or LOCATION items which are not CHECKED
                            it.notificationType == NotificationType.FLOOR || it.notificationType == NotificationType.LOCATION && !it.isChecked
                        }
                        refreshGeofences(items)
                    }
                }
            }
        }
    }

    fun saveItem(
        id: Long,
        selectedName: String,
        notificationType: NotificationType,
        checked: Boolean,
        time: Long?,
        height: Double,
        lat: Double,
        lon: Double,
        tripId: Long
    ) {
        val newItem = PackingItem(
            id = id,
            tripId = tripId,
            name = selectedName,
            notificationType = notificationType,
            isChecked = checked,
            time = time,
            height = height,
            lat = lat,
            lon = lon
        )
        updateItem(newItem)
    }

    fun onSwipeToDelete(selectedItem: PackingItem) {
        _packing.update {
            it?.filterNot { item ->
                item.id == selectedItem.id
            }
        }
        // Perform the delete operation in the repository
        viewModelScope.launch(Dispatchers.IO) {
            roadtripRepository.deleteItem(selectedItem)
        }
    }

    private val _packing = MutableStateFlow<List<PackingItem>?>(null)
    val packing: StateFlow<List<PackingItem>?> = _packing

    fun updateItem(item: PackingItem) {
        viewModelScope.launch(Dispatchers.IO) {
            roadtripRepository.updateItem(item)
        }
    }
    fun insertIntoList(item: PackingItem) {
        viewModelScope.launch(Dispatchers.IO) {
            roadtripRepository.insertIntoList(item)
        }
    }

    fun onPermissionGranted() {
        sensorRepository.permissionsGranted()
    }

    fun onPermissionDenied() {
        sensorRepository.permissionDenied()
    }

    fun saveHeight(height: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            preferences.setHeight(height)
        }
    }
}

sealed interface PackingDataUiState {
    object Loading : PackingDataUiState

    object NoList : PackingDataUiState

    data class Success(val data: RoadtripAndLocationsAndList) : PackingDataUiState
}