package de.kassel.cc22023.roadtrip.ui.packing

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.data.repository.database.NotificationType
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItem

@Composable
fun NewItemDialog(
    currentTrip: Long,
    dialogOpen: MutableState<Boolean>,
    packingViewModel: PackingViewModel = hiltViewModel()
) {
    var name by remember {
        mutableStateOf("")
    }

    if (dialogOpen.value) {
        AlertDialog(
            onDismissRequest = { dialogOpen.value = false },
            title = { Text(text = "Enter item name") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Add a new PackingItem to the packingList
                        val newItem = PackingItem(
                            id = 0,
                            currentTrip,
                            name = name,
                            notificationType = NotificationType.NONE,
                            isChecked = false,
                            null,
                            0.0,
                            0.0,
                            0.0
                        )
                        packingViewModel.insertIntoList(newItem)
                        dialogOpen.value = false
                    }
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogOpen.value = false }) {
                    Text("Cancel")
                }
            },
            text = {
                TextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                )

            }
        )
    }
}