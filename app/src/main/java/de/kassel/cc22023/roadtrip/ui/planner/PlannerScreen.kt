package de.kassel.cc22023.roadtrip.ui.planner

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState

import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState

import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Date

@ExperimentalMaterial3Api
@Composable
fun PlannerScreen() {
    var selectedStartDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedEndDate by remember { mutableStateOf(LocalDate.now()) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // First Date Selection
        var startDate by remember { mutableStateOf(selectedStartDate.toString()) }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = startDate,
                onValueChange = { startDate = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        selectedStartDate = try {
                            LocalDate.parse(startDate)
                        } catch (e: Exception) {
                            LocalDate.now()
                        }
                        showStartDatePicker = false
                    }
                ),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                modifier = Modifier.weight(1f)
            )

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
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = endDate,
                onValueChange = { endDate = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        selectedEndDate = try {
                            LocalDate.parse(endDate)
                        } catch (e: Exception) {
                            LocalDate.now()
                        }
                        showEndDatePicker = false
                    }
                ),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                modifier = Modifier.weight(1f)
            )

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
                openDialog.value = false
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
            DatePicker(state = state)
        }
    }
}
