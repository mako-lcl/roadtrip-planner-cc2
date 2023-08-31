package de.kassel.cc22023.roadtrip.ui.packing

import android.hardware.SensorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mutualmobile.composesensors.rememberPressureSensorState
import de.kassel.cc22023.roadtrip.util.FLOOR_HEIGHT_IN_METERS
import timber.log.Timber

@Composable
fun HeightInput(
    onComplete: () -> Unit,
    viewModel: PackingViewModel = hiltViewModel()
) {
    val pressure = rememberPressureSensorState()

    var floor by remember {
        mutableStateOf("0")
    }

    if (pressure.isAvailable) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val altitude = SensorManager.getAltitude(
                SensorManager.PRESSURE_STANDARD_ATMOSPHERE,
                pressure.pressure
            )
            Text("Current height: $altitude", color = MaterialTheme.colorScheme.onSurfaceVariant)

            Text("Which floor are you on? (0 = ground floor)", color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextField(
                value = floor,
                onValueChange = {
                    floor = it
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            TextButton(
                onClick = {
                    val convertedFloor = floor.toIntOrNull() ?: return@TextButton
                    val convertedHeight = (altitude + convertedFloor * FLOOR_HEIGHT_IN_METERS)
                    Timber.d("converted height: $convertedHeight")
                    // Save the height in SharedPreferences
                    viewModel.saveHeight(convertedHeight)
                    onComplete()
                }
            ) {
                Text("Submit")
            }
        }
    } else {
        Text("Pressure sensor not available")
    }
}