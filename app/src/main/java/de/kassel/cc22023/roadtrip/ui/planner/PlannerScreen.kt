package de.kassel.cc22023.roadtrip.ui.planner

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box



import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.layout.width


import androidx.compose.material3.Button


import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenuItem


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon

import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


import androidx.compose.runtime.derivedStateOf

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp



import de.kassel.cc22023.roadtrip.R

import de.kassel.cc22023.roadtrip.data.local.database.TransportationType
import de.kassel.cc22023.roadtrip.ui.util.CoolLoadingScreen
import de.kassel.cc22023.roadtrip.ui.util.LoadingScreen
import de.kassel.cc22023.roadtrip.util.convertRoadtripFromTestTrip
import de.kassel.cc22023.roadtrip.util.loadRoadtripFromAssets


import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset



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



@ExperimentalMaterial3Api
@Composable
fun PlannerInputScreen(
    viewModel: PlannerViewModel = hiltViewModel()
) {
    val context = LocalContext.current


    var selectedStartDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedEndDate by remember { mutableStateOf(LocalDate.now().plusDays(1)) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var startLocation by remember { mutableStateOf("") }
    var endLocation by remember { mutableStateOf("") }
    var expanded by remember {
        mutableStateOf(false)
    }
    var selectedText by remember {
        mutableStateOf(TransportationType.values().first().value)
    }
    var startDate by remember { mutableStateOf(selectedStartDate.toString()) }
    var endDate by remember { mutableStateOf(selectedEndDate.toString()) }
    var startDateError by remember { mutableStateOf(false) }
    var endDateError by remember { mutableStateOf(false) }
    var endLocationError by remember { mutableStateOf(false) }
    var startLocationError by remember { mutableStateOf(false) }
    val currentDate = remember { LocalDate.now() }
    //val trip by viewModel.trip.collectAsState()
    val image: Painter = painterResource(R.drawable.map_pin)
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
            // First Date Selection

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(

                    value = startLocation,
                    onValueChange = {
                        startLocation = it
                        startLocationError = startLocation == ""
                    },
                    label = { Text(text = "Start", maxLines = 1) },

                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = startLocationError,
                    supportingText = {
                        if (startLocationError) {
                            Text(text = "Can not be empty")
                        }
                    },
                    trailingIcon = {
                        if (startLocationError) {
                            Icon(
                                painter = painterResource(id = R.drawable.error),
                                contentDescription = "Trailing Icon"
                            )
                        }
                    }


                )
                Spacer(modifier = Modifier.width(5.dp))

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    TextField(
                        value = startDate,
                        onValueChange = {},
                        singleLine = true,
                        enabled = false,
                        modifier = Modifier.clickable(onClick = { showStartDatePicker = true }),
                        label = { Text(text = "Start Date") },
                        colors = TextFieldDefaults.colors(

                            disabledContainerColor = if (!startDateError) colorScheme.primaryContainer else colorScheme.errorContainer,
                            disabledTextColor = if (!startDateError) colorScheme.onPrimaryContainer else colorScheme.error,
                            disabledTrailingIconColor = if (!startDateError) colorScheme.onPrimaryContainer else colorScheme.error,
                            disabledSupportingTextColor = colorScheme.error,
                            //errorContainerColor = colorScheme.error,
                            errorTextColor = colorScheme.error


                        ),
                        trailingIcon = {
                            if (!startDateError) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                                    contentDescription = "Trailing Icon"
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.error),
                                    contentDescription = "Trailing Icon"
                                )
                            }
                        },
                        supportingText = {
                            if (startDateError) {
                                Text(text = "Can not be in the past")
                            }
                        },
                        isError = startDateError,


                        )


                }

            }


            if (showStartDatePicker) {
                DatePickerDialogSample(
                    state = rememberDatePickerState(
                        initialSelectedDateMillis = selectedStartDate.toEpochDay() * 24 * 60 * 60 * 1000
                    ),
                    onDateSelected = { newDate ->
                        selectedStartDate =
                            Instant.ofEpochMilli(newDate).atZone(ZoneOffset.UTC).toLocalDate()
                        startDate = selectedStartDate.toString()
                        showStartDatePicker = false
                        startDateError = selectedStartDate.isBefore(currentDate)
                        endDateError = selectedEndDate.isBefore(selectedStartDate.plusDays(1))
                        /*
                        if (selectedStartDate.isBefore(currentDate)) {
                            selectedStartDate = currentDate
                            startDate = currentDate.toString()
                        }
                        if (selectedEndDate.isBefore(selectedStartDate)) {
                            // If the endDate is before the startDate, reset the endDate to match the startDate
                            selectedEndDate = selectedStartDate
                            endDate = selectedStartDate.toString()
                        }*/
                    },
                    onCloseDialog = { showStartDatePicker = false }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Second Date Selection


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = endLocation,
                    onValueChange = {
                        endLocation = it
                        endLocationError = endLocation == ""
                    },
                    label = { Text("Destination") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = endLocationError,
                    supportingText = {
                        if (endLocationError) {
                            Text(text = "Can not be empty")
                        }
                    },
                    trailingIcon = {
                        if (endLocationError) {
                            Icon(
                                painter = painterResource(id = R.drawable.error),
                                contentDescription = "Trailing Icon"
                            )
                        }
                    }

                )
                Spacer(modifier = Modifier.width(5.dp))

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    TextField(
                        value = endDate,
                        onValueChange = {},
                        singleLine = true,
                        enabled = false,
                        modifier = Modifier.clickable(onClick = { showEndDatePicker = true }),
                        label = { Text(text = "End Date") },
                        colors = TextFieldDefaults.colors(

                            disabledContainerColor = if (!endDateError) colorScheme.primaryContainer else colorScheme.errorContainer,
                            disabledTextColor = if (!endDateError) colorScheme.onPrimaryContainer else colorScheme.error,
                            disabledTrailingIconColor = if (!endDateError) colorScheme.onPrimaryContainer else colorScheme.error,
                            disabledSupportingTextColor = colorScheme.error,
                            // errorContainerColor = colorScheme.error,
                            errorTextColor = colorScheme.error


                        ),
                        trailingIcon = {
                            if (!endDateError) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                                    contentDescription = "Trailing Icon"
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.error),
                                    contentDescription = "Trailing Icon"
                                )
                            }
                        },
                        supportingText = {
                            if (endDateError) {
                                Text(text = "Has to be after the Start Date")
                            }
                        },
                        isError = endDateError,


                        )

                }
            }

            if (showEndDatePicker) {
                DatePickerDialogSample(
                    state = rememberDatePickerState(
                        initialSelectedDateMillis = selectedEndDate.toEpochDay() * 24 * 60 * 60 * 1000
                    ),
                    onDateSelected = { newDate ->
                        selectedEndDate =
                            Instant.ofEpochMilli(newDate).atZone(ZoneOffset.UTC).toLocalDate()
                        endDate = selectedEndDate.toString()
                        showEndDatePicker = false
                        endDateError = selectedEndDate.isBefore(selectedStartDate.plusDays(1))
                        /*

                        if (selectedEndDate.isBefore(selectedStartDate)) {
                            // If the endDate is before the startDate, reset the endDate to match the startDate
                            selectedEndDate = selectedStartDate
                            endDate = selectedStartDate.toString()
                        }
                        */


                    },

                    onCloseDialog = { showEndDatePicker = false }

                )
            }



            Spacer(modifier = Modifier.height(40.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = selectedText,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    TransportationType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.value) },
                            onClick = {
                                selectedText = type.value
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }

            Button(onClick = {
                startLocationError = startLocation ==""
                endLocationError = endLocation == ""
                if(!startDateError && !endDateError && !startLocationError && !endLocationError) {
                    viewModel.createRoadtrip(
                        startDate,
                        endDate,
                        startLocation,
                        endLocation,
                        selectedText
                    )
                }
            }) {
                Text("Load from GPT")
            }

            Button(onClick = {
                val testTrip = loadRoadtripFromAssets(context)
                val trip = convertRoadtripFromTestTrip(testTrip)
                viewModel.insertNewRoadtrip(trip)
            }) {
                Text(text = "Load")

            }


        }

    }

}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogSample(
    state: DatePickerState,
    onDateSelected: (Long) -> Unit,
    onCloseDialog: () -> Unit
) {
    // Decoupled snackbar host state from scaffold state for demo purposes.
    val snackState = remember { SnackbarHostState() }
    val snackScope = rememberCoroutineScope()
    SnackbarHost(hostState = snackState, Modifier)
    val openDialog = remember { mutableStateOf(true) }
    // TODO demo how to read the selected date from the state.
    if (openDialog.value) {

        val confirmEnabled = derivedStateOf { state.selectedDateMillis != null }
        DatePickerDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                openDialog.value = false
                onCloseDialog()
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        snackScope.launch {
                            snackState.showSnackbar(
                                "Selected date timestamp: ${state.selectedDateMillis}"
                            )
                        }
                        // Pass the selected date back to the calling composable.
                        onDateSelected(state.selectedDateMillis ?: 0L)
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        onCloseDialog()

                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = state, showModeToggle = false)
        }
    }
}

@JsonClass(generateAdapter = true)
data class TestTrip(
    @Json(name = "start_date")
    val startDate: String,
    @Json(name = "end_date")
    val endDate: String,
    @Json(name = "packing_list")
    val packingList: List<String>,
    @Json(name = "locations")
    val locs: List<Loc>
)

@JsonClass(generateAdapter = true)
data class Loc(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val activities: List<String>
)