package de.kassel.cc22023.roadtrip.ui.planner

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripAndLocationsAndList
import de.kassel.cc22023.roadtrip.util.conditional

@Composable
fun TripSelectionCard(
    trip: RoadtripAndLocationsAndList,
    isSelected: Boolean,
    onNavigateToMap: () -> Unit,
    viewModel: PlannerViewModel = hiltViewModel()
) {
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
    val image: Painter = painterResource(id = drawableId)

    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth() // Card takes full width
            .clickable {
                viewModel.setTrip(trip.trip.id)
                onNavigateToMap()
            }
            .conditional(isSelected) {
                border(6.dp, Color.Red)
            },
    ) {

        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = image,
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Text("From: ${trip.trip.startLocation}")
            Text("To: ${trip.trip.endLocation}")
            Text("By: ${trip.trip.startDate}")
            Text("Until: ${trip.trip.endDate}")
        }
    }
}