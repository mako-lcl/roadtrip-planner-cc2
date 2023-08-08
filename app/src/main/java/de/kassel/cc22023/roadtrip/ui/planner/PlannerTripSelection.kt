package de.kassel.cc22023.roadtrip.ui.planner

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.ui.util.LoadingScreen

@Composable
fun PlannerTripSelection(
    viewModel: PlannerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState is PlannerDataUiState.Success) {
        val trips = (uiState as PlannerDataUiState.Success).data
        LazyColumn() {
            items(trips) { trip ->
                Text("${trip.trip.startLocation}")
            }
        }
    } else {
        LoadingScreen()
    }
}