package de.kassel.cc22023.roadtrip.ui.planner


import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.R
import de.kassel.cc22023.roadtrip.data.local.database.TransportationType
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
            LaunchedEffect(data) {
                Timber.d("Navigating to map!")
                //onNavigateToMap()
                Toast
                    .makeText(context, "Successfully created trip!", Toast.LENGTH_LONG)
                    .show()
            }
        }

        PlannerDataUiState.Error -> {
            PlannerInputScreen()
            LaunchedEffect(data) {
                Toast
                    .makeText(context, "Error while creating trip", Toast.LENGTH_LONG)
                    .show()
            }
        }

        else -> {
            LoadingScreen()
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun PlannerInputScreen(
    viewModel: PlannerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val trip by viewModel.trip.collectAsState()
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


        Column {
            var selectedStartDate by remember { mutableStateOf(LocalDate.now()) }
            var selectedEndDate by remember { mutableStateOf(LocalDate.now()) }
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

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // First Date Selection
                var startDate by remember { mutableStateOf(selectedStartDate.toString()) }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = startLocation,
                        onValueChange = { startLocation = it },
                        label = { Text("Start Location") },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(5.dp))

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = startDate,
                            //fontSize = 16.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Button(
                        onClick = { showStartDatePicker = true },
                        //modifier = Modifier.weight(1f)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.baseline_calendar_month_24),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            colorFilter = ColorFilter.tint(color = Color.White)
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
                        }
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))

                // Second Date Selection
                var endDate by remember { mutableStateOf(selectedEndDate.toString()) }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = endLocation,
                        onValueChange = { endLocation = it },
                        label = { Text("End Location") },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true

                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = endDate,
                            //fontSize = 16.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Button(
                        onClick = { showEndDatePicker = true },
                        //modifier = Modifier.weight(1f)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.baseline_calendar_month_24),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            colorFilter = ColorFilter.tint(color = Color.White)
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
                        }
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
                    viewModel.createRoadtrip(
                        startDate,
                        endDate,
                        startLocation,
                        endLocation,
                        selectedText
                    )
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
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogSample(
    state: DatePickerState,
    onDateSelected: (Long) -> Unit
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
                //openDialog.value = false
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