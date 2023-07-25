package de.kassel.cc22023.roadtrip.ui.planner

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions



import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState


import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState


import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset


@ExperimentalMaterial3Api
@Composable
fun PlannerScreen() {
    val context = LocalContext.current

    Text(text = "Planner")

    val string = context.assets.open("roadtrip.txt").bufferedReader().use{
        it.readText()
    }

    val moshi: Moshi = Moshi.Builder().build()
    val jsonAdapter: JsonAdapter<TestTrip> = moshi.adapter(TestTrip::class.java)
    val trip = jsonAdapter.fromJson(string)

    print(trip)

    var selectedStartDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedEndDate by remember { mutableStateOf(LocalDate.now()) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxWidth()
            .padding(all=30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // First Date Selection
        var startDate by remember { mutableStateOf(selectedStartDate.toString()) }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = startDate,
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            TextButton(onClick = { showStartDatePicker = true }) {
                Text(text = "Choose Start Date")
            }
        }

        if (showStartDatePicker) {
            DatePickerDialogSample(
                state = rememberDatePickerState(
                    initialSelectedDateMillis = selectedStartDate.toEpochDay() * 24 * 60 * 60 * 1000
                ),
                onDateSelected = { newDate ->
                    selectedStartDate = Instant.ofEpochMilli(newDate).atZone(ZoneOffset.UTC).toLocalDate()
                    startDate = selectedStartDate.toString()
                    showStartDatePicker = false
                }
            )
        }

        // Second Date Selection
        var endDate by remember { mutableStateOf(selectedEndDate.toString()) }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = endDate,
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            TextButton(onClick = { showEndDatePicker = true }) {
                Text(text = "Choose End Date")
            }
        }

        if (showEndDatePicker) {
            DatePickerDialogSample(
                state = rememberDatePickerState(
                    initialSelectedDateMillis = selectedEndDate.toEpochDay() * 24 * 60 * 60 * 1000
                ),
                onDateSelected = { newDate ->
                    selectedEndDate = Instant.ofEpochMilli(newDate).atZone(ZoneOffset.UTC).toLocalDate()
                    endDate = selectedEndDate.toString()
                    showEndDatePicker = false
                }
            )
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