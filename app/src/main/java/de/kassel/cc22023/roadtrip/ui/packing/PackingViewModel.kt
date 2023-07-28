package de.kassel.cc22023.roadtrip.ui.packing
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kassel.cc22023.roadtrip.data.RoadtripRepository
import de.kassel.cc22023.roadtrip.data.local.database.NotificationType
import de.kassel.cc22023.roadtrip.data.local.database.PackingItem
import de.kassel.cc22023.roadtrip.data.sensors.SensorRepository
import de.kassel.cc22023.roadtrip.geofence.GeofenceManager
import de.kassel.cc22023.roadtrip.ui.map.MapDataUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
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
    private val geofenceManager: GeofenceManager
) : ViewModel() {
    val location: StateFlow<Location?> = sensorRepository.locationFlow

    private var height = 0.0f// Initialize the reference value to 0
    private var lat = 0.0f// Initialize the reference value to 0
    private var lon = 0.0f// Initialize the reference value to 0

    private fun refreshGeofences(items: List<PackingItem>) {
        if (items.isEmpty()) return

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

    private fun addGeofence(key: Int, location: Location) {
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
                        val items = data.data.filter {
                            it.notificationType == NotificationType.FLOOR || it.notificationType == NotificationType.LOCATION
                        }
                        refreshGeofences(items)
                    }
                }
            }
        }
    }

    fun setHeightAndLocation(value: Float, lat1: Float, lon1: Float) {
        height = value
        lat = lat1
        lon = lon1
    }

    fun getNotificationMessage(sensoralitude: Float, lat1: Float, lon1: Float): String {
        val difference = sensoralitude - height
        return when {
            difference > 5.0 && lat == lat1 && lon == lon1 -> "Up and in Location" // Notify "up" if you are 5 meters above the set value
            difference < -5.0 && lat == lat1 && lon == lon1 -> "Down and in Location" // Notify "down" if you are under the set value
            else -> "" // Empty message if you are within the 5-meter range
        }
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

    val data: StateFlow<PackingDataUiState> =
        roadtripRepository
            .packingList
            .map {
                if (it == null) {
                    PackingDataUiState.Loading
                } else {
                    PackingDataUiState.Success(it)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PackingDataUiState.Loading
            )

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

    }

    fun onPermissionDenied() {

    }
}

sealed interface PackingDataUiState {
    object Loading : PackingDataUiState
    data class Success(val data: List<PackingItem>) : PackingDataUiState
}