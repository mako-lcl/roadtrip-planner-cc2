package de.kassel.cc22023.roadtrip.ui.planner

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.ui.theme.RoadtripTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerTopBar(
    openDialog: () -> Unit,
    viewModel: PlannerViewModel = hiltViewModel()
) {
    TopAppBar(
        title = { Text("Roadtrip Planner") },

        actions = {
            //Add new trip
            IconButton(onClick = { openDialog() }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "New trip"
                )
            }
            //Delete all trips
            IconButton(onClick = { viewModel.deleteAllTrips() }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "New trip"
                )
            }
        },
    )
}

@Preview
@Composable
fun TopBarPreview() {
    RoadtripTheme {
        PlannerTopBar(openDialog = {})
    }
}