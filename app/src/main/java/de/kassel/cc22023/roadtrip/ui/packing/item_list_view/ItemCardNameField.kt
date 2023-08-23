package de.kassel.cc22023.roadtrip.ui.packing.item_list_view

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItem
import de.kassel.cc22023.roadtrip.ui.packing.PackingViewModel

@Composable
fun ItemCardNameField(
    selectedName: MutableState<String>,
    item: PackingItem,
    viewModel: PackingViewModel = hiltViewModel()
) {
    TextField(
        value = selectedName.value,
        onValueChange = {
            selectedName.value = it
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                val saveName = selectedName.value
                viewModel.saveItem(
                    item.id,
                    saveName, // Save the updated text here
                    item.notificationType,
                    item.isChecked,
                    item.time,
                    item.height,
                    item.lat,
                    item.lon,
                    item.tripId,
                    item.image
                )
            }),
        textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
        colors = TextFieldDefaults.colors(
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledTextColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}