package de.kassel.cc22023.roadtrip.ui.packing.item_dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItem
import de.kassel.cc22023.roadtrip.ui.packing.PackingViewModel
import de.kassel.cc22023.roadtrip.ui.util.LoadingScreen

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
        var latitude by remember { mutableStateOf(if (item.lat == 0.0) it.latitude.toString() else item.lat.toString()) }
        var longitude by remember { mutableStateOf(if (item.lon == 0.0) it.longitude.toString() else item.lon.toString()) }
        // State variables to hold latitude and longitude
        var height by remember { mutableStateOf(if (item.height == 0.0) it.altitude.toString() else item.height.toString()) }

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
                val lat = latitude.toDoubleOrNull()
                val lon = longitude.toDoubleOrNull()
                val h = height.toDoubleOrNull()

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

            LocationSelectorView(selectLocation = {
                latitude = it.latitude.toString()
                longitude = it.longitude.toString()
            })
        }
    }
}
