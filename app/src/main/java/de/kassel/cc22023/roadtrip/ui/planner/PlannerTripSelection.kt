package de.kassel.cc22023.roadtrip.ui.planner

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripAndLocationsAndList
import de.kassel.cc22023.roadtrip.ui.map.NoTripScreen
import de.kassel.cc22023.roadtrip.ui.util.LoadingScreen

@Composable
fun PlannerTripSelection(
    onNavigateToMap: () -> Unit,
    onNavigateToList: () -> Unit,
    viewModel: PlannerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState is PlannerDataUiState.Success) {
        val trips = (uiState as PlannerDataUiState.Success).data

        if (trips.isEmpty()) {
            NoTripScreen()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
            ) {
                items(trips) { trip ->
                    TripSelectionCard(trip, onNavigateToMap, onNavigateToList)
                }
            }
        }
    } else {
        LoadingScreen()
    }
}

@Composable
fun TripSelectionCard(
    trip: RoadtripAndLocationsAndList,
    onNavigateToMap: () -> Unit,
    onNavigateToList: () -> Unit,
    viewModel: PlannerViewModel = hiltViewModel()
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Box(modifier = Modifier.height(50.dp))
        Text("Start: ${trip.trip.startLocation}")
        Text("End: ${trip.trip.endLocation}")
        Text("Begin: ${trip.trip.startDate}")
        Text("Until: ${trip.trip.endDate}")
        Button(onClick = {
            viewModel.setTrip(trip.trip.id)
            onNavigateToMap()
        }) {
            Text("Map")
        }
        Button(onClick = {
            viewModel.setTrip(trip.trip.id)
            onNavigateToList()
        }) {
            Text("List")
        }
        Button(onClick = { viewModel.deleteTrip(trip) }) {
            Text("Delete")
        }
    }
}