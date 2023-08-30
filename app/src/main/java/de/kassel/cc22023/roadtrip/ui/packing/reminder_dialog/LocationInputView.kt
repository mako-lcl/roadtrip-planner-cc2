package de.kassel.cc22023.roadtrip.ui.packing.reminder_dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.data.repository.database.NotificationType
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItem
import de.kassel.cc22023.roadtrip.ui.packing.PackingViewModel
import de.kassel.cc22023.roadtrip.ui.util.LoadingScreen

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
        var latitude by remember { mutableStateOf(if (item.lat == 0.0) it.latitude.toString() else item.lat.toString()) }
        var longitude by remember { mutableStateOf(if (item.lon == 0.0) it.longitude.toString() else item.lon.toString()) }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(item.name)

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
                    item.lat = lat.toDouble()
                    item.lon = lon.toDouble()
                    item.notificationType = NotificationType.LOCATION

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