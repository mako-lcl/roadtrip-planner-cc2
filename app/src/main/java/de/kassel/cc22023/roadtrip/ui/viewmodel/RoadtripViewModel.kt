package de.kassel.cc22023.roadtrip.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kassel.cc22023.roadtrip.data.RoadtripRepository
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripData
import javax.inject.Inject

@HiltViewModel
class WeatherDataViewModel @Inject constructor(
    private val roadtripRepository: RoadtripRepository
) : ViewModel() {

sealed interface RoadtripUiState {
        object Loading : RoadtripUiState
        data class Success(val data: RoadtripData) : RoadtripUiState
    }
}
