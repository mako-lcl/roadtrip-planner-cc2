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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val roadtripRepository: RoadtripRepository,
    private val preferences: PreferenceStore
) : ViewModel() {
    private val _requestStatus: MutableStateFlow<PlannerRequestStatus> = MutableStateFlow(PlannerRequestStatus.Idle)
    val requestStatus: StateFlow<PlannerRequestStatus> = _requestStatus

    val uiState: StateFlow<PlannerDataUiState> = combine(
        roadtripRepository.allRoadtrips,
        preferences.currentTrip
    ) { trips, currentTripIndex ->
        if (trips != null) {
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
            _requestStatus.value = PlannerRequestStatus.Idle
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
                    viewModelScope.launch(Dispatchers.IO) {
                        val tripId = roadtripRepository.insertNewRoadtrip(it) {
                            viewModelScope.launch(Dispatchers.IO) {
                                roadtripRepository.updatePhotos(it)
                            }
                        }
                        preferences.setCurrentTrip(tripId)

                        _requestStatus.value = PlannerRequestStatus.Success
                    }
                },
                onLoading = {
                    _requestStatus.value = PlannerRequestStatus.Loading
                },
                onError = {
                    _requestStatus.value = PlannerRequestStatus.Error
                }
            )
        }
    }

    fun insertTestRoadtrip(trip: RoadtripAndLocationsAndList) {
        viewModelScope.launch(Dispatchers.IO) {
            _requestStatus.value = PlannerRequestStatus.Success
            roadtripRepository.insertNewRoadtrip(trip) {// insert roadtrip
                viewModelScope.launch(Dispatchers.IO) {
                    roadtripRepository.updatePhotos(it)
                }
            }
            preferences.setCurrentTrip(trip.trip.id)
        }
    }

    fun deleteAllTrips() {
        viewModelScope.launch(Dispatchers.IO) {
            roadtripRepository.deleteAllTrips()
            preferences.setCurrentTrip(-1)
        }
    }

    fun setTrip(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            preferences.setCurrentTrip(id)
        }
    }

    fun deleteTrip(trip: RoadtripAndLocationsAndList) {
        viewModelScope.launch(Dispatchers.IO) {
            roadtripRepository.deleteTrip(trip)
        }
    }
}

sealed interface PlannerDataUiState {
    object FetchingDatabase : PlannerDataUiState

    data class Success(val data: List<RoadtripAndLocationsAndList>, val current: Long) :
        PlannerDataUiState
}

sealed interface PlannerRequestStatus {
    object Idle : PlannerRequestStatus
    object Loading : PlannerRequestStatus
    object Success : PlannerRequestStatus
    object Error : PlannerRequestStatus
}