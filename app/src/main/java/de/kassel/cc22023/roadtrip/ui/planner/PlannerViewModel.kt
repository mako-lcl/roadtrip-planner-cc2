package de.kassel.cc22023.roadtrip.ui.planner

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kassel.cc22023.roadtrip.data.RoadtripRepository
import de.kassel.cc22023.roadtrip.data.local.database.CombinedRoadtrip
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val roadtripRepository: RoadtripRepository
) : ViewModel() {
    private val _trip = MutableStateFlow<CombinedRoadtrip?>(null)
    val trip: StateFlow<CombinedRoadtrip?> = _trip

    fun getRoadtrip() {
        viewModelScope.launch(Dispatchers.IO) {
            _trip.value = roadtripRepository.getRoadtrip()
        }
    }

    fun insertNewRoadtrip(trip: CombinedRoadtrip) {
        viewModelScope.launch(Dispatchers.IO) {
            roadtripRepository.insertNewRoadtrip(trip)
        }
    }
}
