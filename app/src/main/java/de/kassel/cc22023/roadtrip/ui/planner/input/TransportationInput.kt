package de.kassel.cc22023.roadtrip.ui.planner.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import de.kassel.cc22023.roadtrip.data.repository.database.TransportationType
import de.kassel.cc22023.roadtrip.ui.util.SegmentedControl
import timber.log.Timber

@Composable
fun TransportationInput(
    selectedText: MutableState<String>
) {
    SegmentedControl(items = TransportationType.values().toList(), onItemSelection = {index ->
        TransportationType.values().getOrNull(index)?.let {
            selectedText.value = it.value
            Timber.d(it.value)
        }
    })
}