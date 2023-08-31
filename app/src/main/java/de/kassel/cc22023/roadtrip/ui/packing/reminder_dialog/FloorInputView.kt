package de.kassel.cc22023.roadtrip.ui.packing.reminder_dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mutualmobile.composesensors.rememberPressureSensorState
import de.kassel.cc22023.roadtrip.data.repository.database.NotificationType
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItem
import de.kassel.cc22023.roadtrip.ui.packing.PackingViewModel
import de.kassel.cc22023.roadtrip.ui.util.LoadingScreen

@Composable
fun FloorInputView(
    item: PackingItem,
    closeDialog: () -> Unit,
    onSettings: () -> Unit,
    viewModel: PackingViewModel = hiltViewModel()
) {
    val location by viewModel.location.collectAsState()
    val pressure = rememberPressureSensorState()

    if (location == null) {
        LoadingScreen()
    }

    location?.let {
            // State variables to hold latitude and longitude
            var latitude by remember { mutableStateOf(if (item.lat == 0.0) it.latitude.toString() else item.lat.toString()) }
            var longitude by remember { mutableStateOf(if (item.lon == 0.0) it.longitude.toString() else item.lon.toString()) }
            // State variables to hold latitude and longitude
            var floor by remember { mutableStateOf("0") }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (pressure.isAvailable) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = {
                                onSettings()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                    }
                }

                Text(item.name,color = MaterialTheme.colorScheme.onSurface)

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
                    value = floor,
                    onValueChange = { newValue -> floor = newValue },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text(text = "Floor (0 = ground level)") },
                )

                // Button to submit location
                Button(onClick = {
                    // Process the latitude and longitude values here (e.g., save to database)
                    // For example, you can convert them to Double values like this:
                    val lat = latitude.toDoubleOrNull()
                    val lon = longitude.toDoubleOrNull()
                    val f = floor.toIntOrNull()

                    if (lat != null && lon != null && f != null) {
                        // Do something with the latitude and longitude values, e.g., save to the item
                        item.lat = lat
                        item.lon = lon
                        item.floor = f
                        item.notificationType = NotificationType.FLOOR
                        // Update the item using the viewModel
                        viewModel.updateItem(item)
                        closeDialog()
                    } else {
                        // Handle invalid latitude or longitude input
                    }
                }) {
                    Text(text = "Submit")
                }

                LocationSelectorView(selectLocation = {
                    latitude = it.latitude.toString()
                    longitude = it.longitude.toString()
                })
            }
    }
}
