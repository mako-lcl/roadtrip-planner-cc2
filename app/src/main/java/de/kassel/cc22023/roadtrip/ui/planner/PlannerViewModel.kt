package de.kassel.cc22023.roadtrip.ui.planner

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kassel.cc22023.roadtrip.data.RoadtripRepository
import de.kassel.cc22023.roadtrip.data.local.database.CombinedRoadtrip
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripData
import de.kassel.cc22023.roadtrip.data.local.database.TransportationType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val roadtripRepository: RoadtripRepository
) : ViewModel() {
    private val _trip = MutableStateFlow<PlannerDataUiState?>(PlannerDataUiState.Idle)
    val trip: StateFlow<PlannerDataUiState?> = _trip

    fun getRoadtrip() {
        viewModelScope.launch(Dispatchers.IO) {
            //_trip.value = roadtripRepository.getRoadtrip()
        }
    }

    fun createRoadtrip(startLocation: String, endLocation: String, startDate: String, endDate: String, transportationType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            roadtripRepository.createRoadtrip(
                startLocation,
                endLocation,
                startDate,
                endDate,
                transportationType,
                onSuccess = {
                    insertRoadtrip(it)
                    _trip.value = PlannerDataUiState.Success(it)
                },
                onLoading = {
                            _trip.value = PlannerDataUiState.Loading
                },
                onError = {
                    _trip.value = PlannerDataUiState.Error
                }
            )
        }
    }

    fun insertRoadtrip(combinedRoadtrip: CombinedRoadtrip) {
        viewModelScope.launch(Dispatchers.IO) {
            roadtripRepository.insertNewRoadtrip(combinedRoadtrip)
        }
    }

    fun insertNewRoadtrip(trip: CombinedRoadtrip) {
        viewModelScope.launch(Dispatchers.IO) {
            roadtripRepository.insertNewRoadtrip(trip)
        }
    }
}

sealed interface PlannerDataUiState {
    object Idle : PlannerDataUiState
    object Loading : PlannerDataUiState
    data class Success(val data: CombinedRoadtrip) : PlannerDataUiState

    object Error : PlannerDataUiState
}
