package de.kassel.cc22023.roadtrip.ui.planner

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripAndLocationsAndList
import de.kassel.cc22023.roadtrip.ui.map.NoTripScreen
import de.kassel.cc22023.roadtrip.ui.packing.item_list_view.PackingItemCard
import de.kassel.cc22023.roadtrip.ui.util.LoadingScreen
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun PlannerTripSelection(
    onNavigateToMap: () -> Unit,
    viewModel: PlannerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState is PlannerDataUiState.Success) {
        val trips = (uiState as PlannerDataUiState.Success).data
        val current = (uiState as PlannerDataUiState.Success).current

        if (trips.isEmpty()) {
            NoTripScreen()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
            ) {
                items(trips) { trip ->
                    val delete = SwipeAction(
                        onSwipe = {
                            viewModel.deleteTrip(trip)
                        },
                        icon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete chat",
                                modifier = Modifier.padding(16.dp),
                                tint = Color.White
                            )
                        }, background = Color.Red.copy(alpha = 0.5f),
                        isUndo = true
                    )
                    SwipeableActionsBox(
                        modifier = Modifier,
                        swipeThreshold = 100.dp,
                        endActions = listOf(delete)
                    ) {
                        TripSelectionCard(trip, trip.trip.id == current, onNavigateToMap)
                    }
                }
            }
        }
    } else {
        LoadingScreen()
    }
}