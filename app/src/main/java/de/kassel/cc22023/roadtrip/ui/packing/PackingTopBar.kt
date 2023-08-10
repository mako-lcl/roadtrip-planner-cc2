package de.kassel.cc22023.roadtrip.ui.packing

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.R
import de.kassel.cc22023.roadtrip.data.repository.database.NotificationType
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItem
import de.kassel.cc22023.roadtrip.ui.theme.RoadtripTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackingTopBar(
    openSettings: () -> Unit,
    viewModel: PackingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentTrip by viewModel.currentTripId.collectAsState(initial = -1L)

    TopAppBar(
        title = { Text(context.getString(R.string.packing_top_title)) },

        actions = {
            //Add new packing item
            IconButton(onClick = {
                if (currentTrip != -1L) {
                    // Add a new PackingItem to the packingList
                    val newItem = PackingItem(
                        id = 0,
                        currentTrip,
                        name = "",
                        notificationType = NotificationType.NONE,
                        isChecked = false,
                        null,
                        0.0,
                        0.0,
                        0.0
                    )
                    viewModel.insertIntoList(newItem)
                } else {
                    Toast
                        .makeText(context, "Error while creating item", Toast.LENGTH_LONG)
                        .show()
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "New trip"
                )
            }
            //Height Settings
            IconButton(onClick = {  openSettings() }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "New trip"
                )
            }
        },
    )
}