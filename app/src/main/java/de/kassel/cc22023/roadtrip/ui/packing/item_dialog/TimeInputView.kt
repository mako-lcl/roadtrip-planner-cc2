package de.kassel.cc22023.roadtrip.ui.packing.item_dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.alarm.setAlarm
import de.kassel.cc22023.roadtrip.data.local.database.PackingItem
import de.kassel.cc22023.roadtrip.ui.packing.PackingViewModel
import de.kassel.cc22023.roadtrip.ui.util.DatePickerDialog
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeInputView(
    item: PackingItem,
    closeDialog: () -> Unit,
    viewModel: PackingViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    Text(
        "Add Time Notification",
        fontSize = 30.sp
    )

    var startDate = LocalDate.now()
    item.time?.let {
        startDate = LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC).toLocalDate()
    }

    var selectedStartDate by remember { mutableStateOf(startDate) }
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    var showStartDatePicker by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box {
            TextField(
                value = selectedStartDate.toString(),
                onValueChange = {},
                singleLine = true,
                enabled = false,
                modifier = Modifier.clickable(onClick = { showStartDatePicker = true }),
                label = { Text(text = "Date") },
            )
        }
        if (showStartDatePicker) {
            DatePickerDialog(
                state = rememberDatePickerState(
                    initialSelectedDateMillis = selectedStartDate.toEpochDay() * 1000 * 24 * 3600
                ),
                onDateSelected = { newDate ->
                    selectedStartDate =
                        Instant.ofEpochMilli(newDate).atZone(ZoneOffset.UTC)
                            .toLocalDate()
                    showStartDatePicker = false
                },
                onCloseDialog = { showStartDatePicker = false }
            )
        }


        var time = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        item.time?.let {
            time = it
        }
        var showTimePicker by remember { mutableStateOf(false) }

        var selectedDateSeconds by remember { mutableStateOf(time) }
        val timeState = rememberTimePickerState(
            initialHour = LocalDateTime.ofEpochSecond(selectedDateSeconds, 0, ZoneOffset.UTC).hour ,
            initialMinute = LocalDateTime.ofEpochSecond(selectedDateSeconds, 0, ZoneOffset.UTC).minute,
            is24Hour = true
        )
        var hour by remember { mutableStateOf(Calendar.HOUR) }
        var minute by remember { mutableStateOf(Calendar.MINUTE) }

        Box {
            TextField(
                value = LocalDateTime.ofEpochSecond(selectedDateSeconds, 0, ZoneOffset.UTC).format(formatter),
                onValueChange = {},
                singleLine = true,
                enabled = false,
                modifier = Modifier.clickable(onClick = { showTimePicker = true }),
                label = { Text(text = "Time") },
            )
        }

        if (showTimePicker) {
            TimePickerDialog(
                onCancel = { showTimePicker = false },
                onConfirm = {

                    hour = timeState.hour
                    minute = timeState.minute
                    val newTime = LocalDateTime.of(selectedStartDate, LocalTime.of(hour, minute))
                    selectedDateSeconds = newTime.toEpochSecond(ZoneOffset.UTC)
                    showTimePicker = false

                }
            ) {
                TimePicker(state = timeState)
            }
        }

        // Display selected date and time
        Button(onClick = {
            val newTime = LocalDateTime.of(selectedStartDate, LocalTime.of(hour, minute))
            item.time = newTime.toEpochSecond(ZoneOffset.UTC)
            viewModel.updateItem(item)
            setAlarm(item, context)

            closeDialog()
            // Use the formattedDateTime for further processing or save it as required
        }) {
            Text(text = "Confirm Date and Time")
        }
    }
}
