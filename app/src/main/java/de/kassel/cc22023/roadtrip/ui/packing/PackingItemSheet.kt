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
    closeSheet: () -> Unit,
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
                // State variables to hold latitude and longitude
                var latitude by remember { mutableStateOf("") }
                var longitude by remember { mutableStateOf("") }
                var height by remember { mutableStateOf("") }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // TextField for entering latitude
                    TextField(
                        value = latitude,
                        onValueChange = { newValue -> latitude = newValue },
                        singleLine = true,
                        label = { Text(text = "Latitude") },
                    )

                    // TextField for entering longitude
                    TextField(
                        value = longitude,
                        onValueChange = { newValue -> longitude = newValue },
                        singleLine = true,
                        label = { Text(text = "Longitude") },
                    )
                    TextField(
                        value = height,
                        onValueChange = { newValue -> height = newValue },
                        singleLine = true,
                        label = { Text(text = "Height (in sealevel)") },
                    )

                    // Button to submit location
                    Button(onClick = {
                        // Process the latitude and longitude values here (e.g., save to database)
                        // For example, you can convert them to Double values like this:
                        val lat = latitude.toFloatOrNull()
                        val lon = longitude.toFloatOrNull()
                        val h = height.toFloatOrNull()

                        if (lat != null && lon != null && h != null) {
                            // Do something with the latitude and longitude values, e.g., save to the item
                            item.lat = lat
                            item.lon = lon
                            item.height = h
                            // Update the item using the viewModel
                            viewModel.updateItem(item)
                            closeSheet()
                        } else {
                            // Handle invalid latitude or longitude input
                        }
                    }) {
                        Text(text = "Submit Location")
                    }
                }
            }

            "Location" -> {
                // State variables to hold latitude and longitude
                var latitude by remember { mutableStateOf("") }
                var longitude by remember { mutableStateOf("") }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // TextField for entering latitude
                    TextField(
                        value = latitude,
                        onValueChange = { newValue -> latitude = newValue },
                        singleLine = true,
                        label = { Text(text = "Latitude") },
                    )

                    // TextField for entering longitude
                    TextField(
                        value = longitude,
                        onValueChange = { newValue -> longitude = newValue },
                        singleLine = true,
                        label = { Text(text = "Longitude") },
                    )

                    // Button to submit location
                    Button(onClick = {
                        // Process the latitude and longitude values here (e.g., save to database)
                        // For example, you can convert them to Double values like this:
                        val lat = latitude.toDoubleOrNull()
                        val lon = longitude.toDoubleOrNull()

                        if (lat != null && lon != null) {
                            // Do something with the latitude and longitude values, e.g., save to the item
                            item.lat = lat.toFloat()
                            item.lon = lon.toFloat()

                            // Update the item using the viewModel
                            viewModel.updateItem(item)
                            closeSheet()
                        } else {
                            // Handle invalid latitude or longitude input
                        }
                    }) {
                        Text(text = "Submit Location")
                    }
                }
            }

            "Time" -> {

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
                var clockSeconds by remember { mutableStateOf(0L) }

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

                    val mCalendar = Calendar.getInstance()
                    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
                    val mMinute = mCalendar[Calendar.MINUTE]
                    val defaultDateTime = LocalDateTime.now()
                    val dateTime by remember { mutableStateOf(defaultDateTime) }
                    var selectedDateSeconds by remember { mutableStateOf(LocalDate.now().toEpochDay()) }

                    val mTimePickerDialog = TimePickerDialog(
                        mContext,
                        { _, mHour: Int, mMinute: Int ->
                            val newTime = LocalDateTime.of(selectedStartDate, LocalTime.of(mHour, mMinute))
                            selectedDateSeconds = newTime.toEpochSecond(ZoneOffset.UTC)
                        }, mHour, mMinute, false
                    )

                    val formatter = DateTimeFormatter.ofPattern("HH:mm")

                    Box {
                        TextField(
                            value = LocalDateTime.ofEpochSecond(selectedDateSeconds, 0, ZoneOffset.UTC).format(formatter),
                            onValueChange = {},
                            singleLine = true,
                            enabled = false,
                            modifier = Modifier.clickable(onClick = { mTimePickerDialog.show(); clock = dateTime.toString() }),
                            label = { Text(text = "Time") },
                        )
                    }
                    // Display selected date and time
                    Button(onClick = {

                        item.time = selectedDateSeconds
                        viewModel.updateItem(item)
                        closeSheet()
                        // Use the formattedDateTime for further processing or save it as required
                    }) {
                        Text(text = "Confirm Date and Time")
                    }
                }
            }
        }
    }
    
}

