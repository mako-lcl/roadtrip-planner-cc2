package de.kassel.cc22023.roadtrip.ui.planner

import android.content.res.Resources
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.R
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
                columns = GridCells.Fixed(1),
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
            .padding(10.dp)
            .fillMaxWidth() // Card takes full width

    ) {
        Box(modifier = Modifier.height(10.dp))
        val tripNumber = trip.trip.id % 10 + 1 // Adjust this based on your trip ID range
        val imageName = "roadtrip$tripNumber"
        val context = LocalContext.current
        val drawableId = remember(imageName) {
            context.resources.getIdentifier(
                imageName,
                "drawable",
                context.packageName
            )
        }
        val image : Painter = painterResource(id = drawableId)
        Image(
            painter = image,
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box {
                    Text("Start: ${trip.trip.startLocation}")
                }
                Spacer(Modifier.weight(1f))
                Box {
                    Text("Begin: ${trip.trip.startDate}")
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box {
                    Text("End: ${trip.trip.endLocation}")
                }
                Spacer(Modifier.weight(1f))
                Box {
                    Text("End: ${trip.trip.endDate}")
                }
            }
        }
Row(modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly){
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


}