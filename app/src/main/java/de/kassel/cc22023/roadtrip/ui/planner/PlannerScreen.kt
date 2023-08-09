package de.kassel.cc22023.roadtrip.ui.planner


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.kassel.cc22023.roadtrip.ui.theme.RoadtripTheme

@Composable
fun PlannerScreen(
    onNavigateToMap: () -> Unit,
    onNavigateToList: () -> Unit,
) {
    PlannerTripSelection(onNavigateToMap, onNavigateToList)
}


@Preview
@Composable
fun PlannerScreenPreview() {
    RoadtripTheme {
        PlannerScreen(onNavigateToList = {}, onNavigateToMap = {})
    }
}