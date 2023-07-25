package de.kassel.cc22023.roadtrip.ui.packing
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kassel.cc22023.roadtrip.data.RoadtripRepository
import de.kassel.cc22023.roadtrip.data.local.database.PackingItem
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripData
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PackingViewModel @Inject constructor(
    private val roadtripRepository: RoadtripRepository
) : ViewModel() {
    fun updateCheckBoxState(item: PackingItem) {
        viewModelScope.launch {
            roadtripRepository.updateCheckbox(item)
        }
    }
    fun insertIntoList(item: PackingItem) {
        viewModelScope.launch {
            roadtripRepository.insertIntoList(item)
        }
    }
}
