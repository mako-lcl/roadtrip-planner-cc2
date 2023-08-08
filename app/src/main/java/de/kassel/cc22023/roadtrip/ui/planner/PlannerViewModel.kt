package de.kassel.cc22023.roadtrip.ui.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kassel.cc22023.roadtrip.data.preferences.PreferenceStore
import de.kassel.cc22023.roadtrip.data.repository.RoadtripRepository
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripAndLocationsAndList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val roadtripRepository: RoadtripRepository,
    private val preferences: PreferenceStore
) : ViewModel() {
    private val _requestStatus = MutableStateFlow(PlannerRequestStatus.Idle)
    private val requestStatus: StateFlow<PlannerRequestStatus> = _requestStatus

    val uiState = combine(
        roadtripRepository.allRoadtrips,
        preferences.currentTrip,
        requestStatus
    ) { trips, currentTripIndex, status ->
        if (status == PlannerRequestStatus.Loading) {
            PlannerDataUiState.Loading
        }
        else if (trips != null) {
            PlannerDataUiState.Success(trips, currentTripIndex)
        } else {
            PlannerDataUiState.FetchingDatabase
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlannerDataUiState.FetchingDatabase
        )

    fun resetToIdle() {
        viewModelScope.launch(Dispatchers.IO) {
            //_trip.value = PlannerDataUiState.Idle
        }
    }

    fun createRoadtrip(
        startLocation: String,
        endLocation: String,
        startDate: String,
        endDate: String,
        transportationType: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            roadtripRepository.createRoadtrip(
                startLocation,
                endLocation,
                startDate,
                endDate,
                transportationType,
                onSuccess = {
                    insertRoadtrip(it)
                    //_trip.value = PlannerDataUiState.Success(it)
                },
                onLoading = {
                    //_trip.value = PlannerDataUiState.Loading
                },
                onError = {
                    //_trip.value = PlannerDataUiState.Error
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
            //_trip.value = PlannerDataUiState.Success(trip)
            roadtripRepository.insertNewRoadtrip(trip)
        }
    }
}

sealed interface PlannerDataUiState {
    object Idle : PlannerDataUiState
    object Loading : PlannerDataUiState

    object FetchingDatabase : PlannerDataUiState

    data class Success(val data: List<RoadtripAndLocationsAndList>, val current: Int) :
        PlannerDataUiState

    object Error : PlannerDataUiState
}

sealed interface PlannerRequestStatus {
    object Idle : PlannerRequestStatus
    object Loading : PlannerRequestStatus
    object Success : PlannerRequestStatus
    object Error : PlannerRequestStatus
}