package de.kassel.cc22023.roadtrip.ui.planner.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.R
import de.kassel.cc22023.roadtrip.data.repository.database.TransportationType
import de.kassel.cc22023.roadtrip.ui.planner.PlannerViewModel
import de.kassel.cc22023.roadtrip.util.convertRoadtripFromTestTrip
import de.kassel.cc22023.roadtrip.util.loadRoadtripFromAssets
import java.time.LocalDate

@ExperimentalMaterial3Api
@Composable
fun PlannerInputScreen(
    viewModel: PlannerViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    //Background image
    val image: Painter = painterResource(R.drawable.map_pin)

    /*
    *  Start Date Input States
    */
    val selectedStartDate = remember { mutableStateOf(LocalDate.now()) }
    val showStartDatePicker = remember { mutableStateOf(false) }
    val startDate = remember { mutableStateOf(selectedStartDate.value.toString()) }
    val startDateError = remember { mutableStateOf(false) }

    /*
    *  End Date Input States
    */
    val selectedEndDate = remember { mutableStateOf(LocalDate.now().plusDays(4)) }
    val showEndDatePicker = remember { mutableStateOf(false) }
    val endDate = remember { mutableStateOf(selectedEndDate.value.toString()) }
    val endDateError = remember { mutableStateOf(false) }

    /*
    *  Start Location Input States
    */
    val startLocation = remember { mutableStateOf("") }
    val startLocationError = remember { mutableStateOf(false) }

    /*
    *  End Location Input States
    */
    val endLocation = remember { mutableStateOf("") }
    val endLocationError = remember { mutableStateOf(false) }

    /*
    *  Transportation Input States
    */
    val expanded = remember {
        mutableStateOf(false)
    }
    val selectedText = remember {
        mutableStateOf(TransportationType.values().first().value)
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(),
        exit = fadeOut()
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Background image
            Image(
                painter = image,
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(50.dp))

                //Input Title
                InputTitle()

                Spacer(modifier = Modifier.height(50.dp))

                //Start Location
                LocationInput(startLocation, startLocationError, context.getString(R.string.start_location_prompt))
                //End Location
                LocationInput(endLocation, endLocationError, context.getString(R.string.end_location_prompt))

                Spacer(modifier = Modifier.height(20.dp))

                // First Date Selection
                DateInput(startDate, showStartDatePicker, startDateError, selectedStartDate, startDateError, selectedStartDate, endDateError, selectedEndDate, context.getString(R.string.start_date_prompt))
                // Second Date Selection
                DateInput(endDate, showEndDatePicker, endDateError, selectedEndDate, startDateError, selectedStartDate, endDateError, selectedEndDate, context.getString(R.string.end_date_prompt))

                Spacer(modifier = Modifier.height(40.dp))

                TransportationInput(expanded, selectedText)

                Spacer(modifier = Modifier.height(20.dp))

                InputSubmitButton(startLocationError, endLocationError, startLocation, endLocation, startDateError, endDateError, startDate, endDate, selectedText)

                Button(onClick = {
                    val testTrip = loadRoadtripFromAssets(context)
                    val trip = convertRoadtripFromTestTrip(testTrip)
                    viewModel.insertTestRoadtrip(trip)
                }) {
                    Text(text = "Load from Disk")
                }
            }
        }
    }
}