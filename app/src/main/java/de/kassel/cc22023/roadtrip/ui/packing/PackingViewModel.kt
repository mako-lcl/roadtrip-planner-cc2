package de.kassel.cc22023.roadtrip.ui.packing
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kassel.cc22023.roadtrip.data.RoadtripRepository
import de.kassel.cc22023.roadtrip.data.local.database.PackingItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PackingViewModel @Inject constructor(
    private val roadtripRepository: RoadtripRepository
) : ViewModel() {

    private var height = 0.0f// Initialize the reference value to 0
    private var lat = 0.0f// Initialize the reference value to 0
    private var lon = 0.0f// Initialize the reference value to 0

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

    fun updateCheckBoxState(item: PackingItem) {
        viewModelScope.launch(Dispatchers.IO) {
            roadtripRepository.updateCheckbox(item)
        }
    }
    fun insertIntoList(item: PackingItem) {
        viewModelScope.launch(Dispatchers.IO) {
            roadtripRepository.insertIntoList(item)
        }
    }

    fun deleteItem(card: PackingItem) {
        viewModelScope.launch(Dispatchers.IO) {
            roadtripRepository.deleteItem(card)
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