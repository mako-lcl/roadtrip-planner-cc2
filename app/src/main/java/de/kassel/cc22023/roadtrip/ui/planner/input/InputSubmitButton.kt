package de.kassel.cc22023.roadtrip.ui.planner.input

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.ui.planner.PlannerViewModel
import java.time.LocalDate

@Composable
fun InputSubmitButton(
    startLocationError: MutableState<Boolean>,
    endLocationError: MutableState<Boolean>,
    startLocation: MutableState<String>,
    endLocation: MutableState<String>,
    startDateError: MutableState<Boolean>,
    endDateError: MutableState<Boolean>,
    startDate: MutableState<String>,
    endDate: MutableState<String>,
    selectedText: MutableState<String>,
    viewModel: PlannerViewModel = hiltViewModel()
) {
    Button(onClick = {
        startLocationError.value = startLocation.value == ""
        endLocationError.value = endLocation.value == ""
        if (!startDateError.value && !endDateError.value && !startLocationError.value && !endLocationError.value) {
            viewModel.createRoadtrip(
                startDate.value,
                endDate.value,
                startLocation.value,
                endLocation.value,
                selectedText.value
            )
        }
    }) {
        Text("Create")
    }
}