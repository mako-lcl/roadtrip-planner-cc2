package de.kassel.cc22023.roadtrip.ui.planner

import android.widget.Toast
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.ui.planner.input.PlannerInputScreen
import de.kassel.cc22023.roadtrip.ui.util.CoolLoadingScreen

@ExperimentalMaterial3Api
@Composable
fun NewTripPlannerScreen(
    viewModel: PlannerViewModel = hiltViewModel(),
    onTripFinished: () -> Unit
) {
    val context = LocalContext.current
    val data by viewModel.requestStatus.collectAsState()

    when (data) {
        PlannerRequestStatus.Idle -> {
            PlannerInputScreen()
        }

        PlannerRequestStatus.Loading -> {
            CoolLoadingScreen()
        }

        PlannerRequestStatus.Success -> {
            LaunchedEffect(Unit) {
                onTripFinished()
                viewModel.resetToIdle()
            }
        }

        PlannerRequestStatus.Error -> {
            PlannerInputScreen()
            LaunchedEffect(data) {
                Toast
                    .makeText(context, "Error while creating trip", Toast.LENGTH_LONG)
                    .show()
                viewModel.resetToIdle()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTripDialog(
    newTripDialogOpen: MutableState<Boolean>
) {
    if (newTripDialogOpen.value) {
        AlertDialog(onDismissRequest = { newTripDialogOpen.value = false }) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large
            ) {
                NewTripPlannerScreen {
                    newTripDialogOpen.value = false
                }
            }
        }
    }
}