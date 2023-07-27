import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
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
import de.kassel.cc22023.roadtrip.data.local.database.PackingItem
import de.kassel.cc22023.roadtrip.ui.packing.PackingViewModel
import de.kassel.cc22023.roadtrip.ui.planner.DatePickerDialogSample
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackingItemSheet(
    item: PackingItem,
    viewModel: PackingViewModel = hiltViewModel()
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Adding padding around the column
        horizontalAlignment = Alignment.CenterHorizontally, // Center horizontally
        verticalArrangement = Arrangement.spacedBy(8.dp) // Add space between elements
    ) {
        Text(item.name)

        when (item.notificationType.value) {
            "Floor" -> {
                // Handle case 1
                Text("Type: Case 1")
                // Add additional UI components or logic specific to Case 1 here
            }

            "Location" -> {
                // Handle case 2
                Text("Type: Case 2")
                // Add additional UI components or logic specific to Case 2 here
            }

            "Time" -> {
                // Handle case 3
                Text(
                    "Add Time Notification",
                    fontSize = 30.sp
                )
                var selectedStartDate by remember { mutableStateOf(LocalDate.now()) }
                var date by remember { mutableStateOf(selectedStartDate.toEpochDay()) }
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                val selectedClock by remember { mutableStateOf(LocalDateTime.now().format(formatter)) }
                var clock by remember { mutableStateOf(selectedClock.toString()) }
                var showStartDatePicker by remember { mutableStateOf(false) }
                var clockMilis by remember { mutableStateOf(0L) }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box() {
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
                        DatePickerDialogSample(
                            state = rememberDatePickerState(
                                initialSelectedDateMillis = selectedStartDate.toEpochDay() * 24 * 60 * 60 * 1000
                            ),
                            onDateSelected = { newDate ->
                                selectedStartDate =
                                    Instant.ofEpochMilli(newDate).atZone(ZoneOffset.UTC)
                                        .toLocalDate()
                                date = selectedStartDate.toEpochDay()
                                showStartDatePicker = false
                            },
                            onCloseDialog = { showStartDatePicker = false }
                        )
                    }

                    val mContext = LocalContext.current

                    // Declaring and initializing a calendar
                    val mCalendar = Calendar.getInstance()
                    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
                    val mMinute = mCalendar[Calendar.MINUTE]
                    val defaultDateTime = LocalDateTime.now()
                    val dateTime by remember { mutableStateOf(defaultDateTime) }
                    var selectedTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }


                    // Creating a TimePicker dialog
                    val mTimePickerDialog = TimePickerDialog(
                        mContext,
                        { _, mHour: Int, mMinute: Int ->
                            val newTime = LocalDateTime.of(selectedStartDate, LocalTime.of(mHour, mMinute))
                            selectedTimeMillis = newTime.toInstant(ZoneOffset.UTC).toEpochMilli()
                            clockMilis = (mHour * 1000 * 60 + mMinute * 1000).toLong()
                        }, mHour, mMinute, false
                    )

                    val localTime = Instant.ofEpochMilli(clockMilis).atZone(ZoneOffset.UTC).toLocalTime()
                    val formatter = DateTimeFormatter.ofPattern("HH:mm")
                    val formattedTime = localTime.format(formatter)

                    Box() {
                        TextField(
                            value = Instant.ofEpochMilli(selectedTimeMillis).atZone(ZoneOffset.UTC)
                                .toLocalTime()
                                .format(formatter),
                            onValueChange = {},
                            singleLine = true,
                            enabled = false,
                            modifier = Modifier.clickable(onClick = { mTimePickerDialog.show(); clock = dateTime.toString() }),
                            label = { Text(text = "Time") },
                        )
                    }
                    // Display selected date and time
                    Button(onClick = {

                        item.time = clockMilis + date
                        viewModel.updateItem(item)
                        // Use the formattedDateTime for further processing or save it as required
                    }) {
                        Text(text = "Confirm Date and Time")
                    }
                }
            }

            else -> {
                // Default case
                Text("Unknown Type: ${item.notificationType}")
                // Add additional UI components or logic for unknown types here
            }
        }
    }
    
}

