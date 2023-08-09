package de.kassel.cc22023.roadtrip.ui.planner


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@Composable
fun PlannerScreen() {
    PlannerTripSelection()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerTopBar(
    openDialog: () -> Unit
) {
    TopAppBar(
        title = { Text("Roadtrip Planner") },

        actions = {
            IconButton(onClick = { openDialog() }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "New trip"
                )
            }
        },
    )
}