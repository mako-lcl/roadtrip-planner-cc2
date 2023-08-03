package de.kassel.cc22023.roadtrip.ui.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kassel.cc22023.roadtrip.data.repository.RoadtripRepository
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripAndLocationsAndList
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

    fun resetToIdle() {
        viewModelScope.launch(Dispatchers.IO) {
            _trip.value = PlannerDataUiState.Idle
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

    fun insertRoadtrip(trip: RoadtripAndLocationsAndList) {
        viewModelScope.launch(Dispatchers.IO) {
            roadtripRepository.insertNewRoadtrip(trip)
        }
    }

    fun insertNewRoadtrip(trip: RoadtripAndLocationsAndList) {
        viewModelScope.launch(Dispatchers.IO) {
            _trip.value = PlannerDataUiState.Success(trip)
            roadtripRepository.insertNewRoadtrip(trip)
        }
    }
}

sealed interface PlannerDataUiState {
    object Idle : PlannerDataUiState
    object Loading : PlannerDataUiState
    data class Success(val data: RoadtripAndLocationsAndList) : PlannerDataUiState

    object Error : PlannerDataUiState
}
