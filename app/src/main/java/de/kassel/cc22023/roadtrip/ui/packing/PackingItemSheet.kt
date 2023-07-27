import android.Manifest
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import de.kassel.cc22023.roadtrip.data.local.database.PackingItem
import de.kassel.cc22023.roadtrip.ui.packing.PackingViewModel
import de.kassel.cc22023.roadtrip.ui.planner.DatePickerDialogSample
import de.kassel.cc22023.roadtrip.ui.util.LoadingScreen
import de.kassel.cc22023.roadtrip.util.PermissionBox
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun PermissionBeforeItemSheet(
    item: PackingItem,
    closeDialog: () -> Unit,
) {
    val permissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    PermissionBox(
        permissions = permissions,
        requiredPermissions = listOf(permissions.first()),
        description = "App needs location data",
        onGranted = {
            PackingItemSheet(item, closeDialog)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackingItemSheet(
    item: PackingItem,
    closeDialog: () -> Unit,
    viewModel: PackingViewModel = hiltViewModel()
) {
    viewModel.locationPermissionGranted()

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
                FloorInputView(item, closeDialog)
            }

            "Location" -> {
                LocationInputView(item, closeDialog)
            }

            "Time" -> {
                TimeInputView(item, closeDialog)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeInputView(
    item: PackingItem,
    closeDialog: () -> Unit,
    viewModel: PackingViewModel = hiltViewModel()
) {
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
    val selectedClock by remember { mutableStateOf(LocalDateTime.now().format(formatter)) }
    var clock by remember { mutableStateOf(selectedClock.toString()) }
    var showStartDatePicker by remember { mutableStateOf(false) }

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

        val mContext = LocalContext.current

        var time = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        item.time?.let {
            time = it
        }

        val mCalendar = Calendar.getInstance()
        var mHour = remember { mCalendar[Calendar.HOUR_OF_DAY] }
        var mMinute = remember { mCalendar[Calendar.MINUTE] }
        val defaultDateTime = LocalDateTime.now()
        val dateTime by remember { mutableStateOf(defaultDateTime) }
        var selectedDateSeconds by remember { mutableStateOf(time) }

        val mTimePickerDialog = TimePickerDialog(
            mContext,
            { _, hour: Int, minute: Int ->
                mHour = hour
                mMinute = minute
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
            val newTime = LocalDateTime.of(selectedStartDate, LocalTime.of(mHour, mMinute))
            item.time = newTime.toEpochSecond(ZoneOffset.UTC)
            viewModel.updateItem(item)
            closeDialog()
            // Use the formattedDateTime for further processing or save it as required
        }) {
            Text(text = "Confirm Date and Time")
        }
    }
}

@Composable
fun FloorInputView(
    item: PackingItem,
    closeDialog: () -> Unit,
    viewModel: PackingViewModel = hiltViewModel()
) {
    val location by viewModel.location.collectAsState()

    if (location == null) {
        LoadingScreen()
    }
    location?.let {
        // State variables to hold latitude and longitude
        var latitude by remember { mutableStateOf(if (item.lat == 0f) it.latitude.toString() else item.lat.toString()) }
        var longitude by remember { mutableStateOf(if (item.lon == 0f) it.longitude.toString() else item.lon.toString()) }
        // State variables to hold latitude and longitude
        var height by remember { mutableStateOf(if (item.height == 0f) it.altitude.toString() else item.height.toString()) }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // TextField for entering latitude
            TextField(
                value = latitude,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = { Text(text = "Latitude") },
            )

            // TextField for entering longitude
            TextField(
                value = longitude,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = { Text(text = "Longitude") },
            )
            TextField(
                value = height,
                onValueChange = { newValue -> height = newValue },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    closeDialog()
                } else {
                    // Handle invalid latitude or longitude input
                }
            }) {
                Text(text = "Submit Location")
            }
            LocationSelector(selectLocation = {
                latitude = it.latitude.toString()
                longitude = it.longitude.toString()
            })
        }
    }
}

@Composable
fun LocationInputView(
    item: PackingItem,
    closeDialog: () -> Unit,
    viewModel: PackingViewModel = hiltViewModel()
) {
    val location by viewModel.location.collectAsState()

    if (location == null) {
        LoadingScreen()
    }
    location?.let {
        // State variables to hold latitude and longitude
        var latitude by remember { mutableStateOf(if (item.lat == 0f) it.latitude.toString() else item.lat.toString()) }
        var longitude by remember { mutableStateOf(if (item.lon == 0f) it.longitude.toString() else item.lon.toString()) }

        Column(
            modifier = Modifier.fillMaxSize(),
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
                    closeDialog()
                } else {
                    // Handle invalid latitude or longitude input
                }
            }) {
                Text(text = "Submit Location")
            }

            LocationSelector(selectLocation = {
                latitude = it.latitude.toString()
                longitude = it.longitude.toString()
            })
        }
    }
}

@Composable
fun LocationSelector(
    selectLocation: (LatLng) -> Unit,
    viewModel: PackingViewModel = hiltViewModel()
) {
    val location by viewModel.location.collectAsState()

    if (location != null) {
        val latLng = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(latLng, 10f)
        }

        var selectedLatLng by remember { mutableStateOf(latLng) }

        val markerState = rememberMarkerState(position = selectedLatLng)

        GoogleMap(
            cameraPositionState = cameraPositionState,
            onMapClick = {
                selectedLatLng = it
                markerState.position = selectedLatLng
                selectLocation(it)
            }
        ) {
            MarkerInfoWindow(
                state = markerState,
            )
        }
    } else {
        CircularProgressIndicator()
    }
}

