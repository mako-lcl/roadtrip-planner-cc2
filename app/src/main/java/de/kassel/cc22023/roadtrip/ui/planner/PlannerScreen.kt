package de.kassel.cc22023.roadtrip.ui.planner


import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.ui.planner.input.PlannerInputScreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlannerScreen() {
    PlannerTripSelection()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerTopBar(
    openDialog: () -> Unit
) {
    TopAppBar(
        title = { Text("Roadtrip Planner") },

        actions = {
            IconButton(onClick = { openDialog() }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "New trip"
                )
            }
        },
    )
}

@ExperimentalMaterial3Api
@Composable
fun NewTripPlannerScreen(
    viewModel: PlannerViewModel = hiltViewModel(),
    onNavigateToMap: () -> Unit
) {
    val context = LocalContext.current
    val data by viewModel.uiState.collectAsState()

    PlannerInputScreen()

    //TODO change

    /*when (data) {
        PlannerDataUiState.Idle -> {
            PlannerInputScreen()
        }

        is PlannerDataUiState.Success -> {
            LaunchedEffect(Unit) {
                onNavigateToMap()
                viewModel.resetToIdle()
            }
        }

        PlannerDataUiState.Error -> {
            PlannerInputScreen()
            LaunchedEffect(data) {
                Toast
                    .makeText(context, "Error while creating trip", Toast.LENGTH_LONG)
                    .show()
                viewModel.resetToIdle()
            }
        }

        else -> {
            CoolLoadingScreen()
        }
    }*/
}