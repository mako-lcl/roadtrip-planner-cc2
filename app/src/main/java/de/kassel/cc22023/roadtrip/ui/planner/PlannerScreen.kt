package de.kassel.cc22023.roadtrip.ui.planner


import androidx.compose.runtime.Composable

@Composable
fun PlannerScreen(
    onNavigateToMap: () -> Unit,
) {
    PlannerTripSelection(onNavigateToMap)
}