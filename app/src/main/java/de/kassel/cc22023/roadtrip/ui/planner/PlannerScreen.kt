package de.kassel.cc22023.roadtrip.ui.planner


import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.ui.util.CoolLoadingScreen

@ExperimentalMaterial3Api
@Composable
fun PlannerScreen(
    viewModel: PlannerViewModel = hiltViewModel(),
    onNavigateToMap: () -> Unit
) {
    val context = LocalContext.current
    val data by viewModel.trip.collectAsState()

    when (data) {
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
    }
}