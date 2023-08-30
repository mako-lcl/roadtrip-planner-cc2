package de.kassel.cc22023.roadtrip.ui.planner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon

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
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.R
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripAndLocationsAndList

@OptIn(ExperimentalTextApi::class)
@Composable
fun TripSelectionCard(
    trip: RoadtripAndLocationsAndList,
    isSelected: Boolean,
    onNavigateToMap: () -> Unit,
    viewModel: PlannerViewModel = hiltViewModel()
) {
    val tripNumber = trip.trip.id % 5 + 1 // Adjust this based on your trip ID range
    val imageName = "roadtrip$tripNumber"
    val context = LocalContext.current
    val drawableId = remember(imageName) {
        context.resources.getIdentifier( //TODO deprecated
            imageName,
            "drawable",
            context.packageName
        )
    }
    val image: Painter = painterResource(id = drawableId)
    Box {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth() // Card takes full width
                .clickable {
                    viewModel.setTrip(trip.trip.id)
                    onNavigateToMap()
                },
            border = if (isSelected) BorderStroke(4.dp, Color.White) else null
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.background(de.kassel.cc22023.roadtrip.ui.theme.brushLight
                )
            ) {
                Box(modifier = Modifier.height(200.dp)) {
                    TripSelectionImage(image)
                    if (isSelected) {
                        Image(
                            painter = painterResource(id = R.drawable.selected),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.Center)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_marker),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text("${trip.trip.startLocation}")
                        }
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_down),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_marker),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text("${trip.trip.endLocation}")
                        }
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_date),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text("${trip.trip.startDate}")
                        }
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_down),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_date),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text("${trip.trip.endDate}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TripSelectionImage(image: Painter) {
    Image(
        painter = image,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}