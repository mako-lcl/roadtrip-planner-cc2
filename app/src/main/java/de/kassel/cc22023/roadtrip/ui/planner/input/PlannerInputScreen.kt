package de.kassel.cc22023.roadtrip.ui.planner.input

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
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
import de.kassel.cc22023.roadtrip.R
import de.kassel.cc22023.roadtrip.data.repository.database.TransportationType
import java.time.LocalDate

@ExperimentalMaterial3Api
@Composable
fun PlannerInputScreen() {
    val context = LocalContext.current

    //Background image
    val image: Painter = painterResource(R.drawable.roadtrip5)

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
    val selectedText = remember {
        mutableStateOf(TransportationType.values().first().value)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Background image
        Image(
            painter = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            Modifier
                .fillMaxWidth()
                .padding(all = 16.dp)
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            //Input Title
            InputTitle()

            Spacer(modifier = Modifier.height(32.dp))

            //Start Location
            LocationInput(startLocation, startLocationError, context.getString(R.string.start_location_prompt))
            //End Location
            LocationInput(endLocation, endLocationError, context.getString(R.string.end_location_prompt))

            // First Date Selection
            DateInput(startDate, showStartDatePicker, startDateError, selectedStartDate, startDateError, selectedStartDate, endDateError, selectedEndDate, context.getString(R.string.start_date_prompt))
            // Second Date Selection
            DateInput(endDate, showEndDatePicker, endDateError, selectedEndDate, startDateError, selectedStartDate, endDateError, selectedEndDate, context.getString(R.string.end_date_prompt))

            TransportationInput(selectedText)

            InputSubmitButton(startLocationError, endLocationError, startLocation, endLocation, startDateError, endDateError, startDate, endDate, selectedText)
        }
    }
}