package de.kassel.cc22023.roadtrip.ui.packing
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kassel.cc22023.roadtrip.data.RoadtripRepository
import de.kassel.cc22023.roadtrip.data.local.database.PackingItem
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripData
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
}

sealed interface PackingDataUiState {
    object Loading : PackingDataUiState
    data class Success(val data: List<PackingItem>) : PackingDataUiState
}