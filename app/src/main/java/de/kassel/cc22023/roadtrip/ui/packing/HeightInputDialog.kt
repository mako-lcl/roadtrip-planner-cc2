package de.kassel.cc22023.roadtrip.ui.packing

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HeightInputDialog(
    heightDialogOpen: MutableState<Boolean>,
    viewModel: PackingViewModel = hiltViewModel()
) {
    val height by viewModel.height.collectAsState(initial = 0.0)

    var heightText by remember {
        mutableStateOf("")
    }

    LaunchedEffect(height) {
        heightText = height.toString()
    }

    if (heightDialogOpen.value) {
        AlertDialog(
            onDismissRequest = { heightDialogOpen.value = false },
            title = { Text(text = "Enter Height in meters") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val convertedHeight = heightText.toDoubleOrNull() ?: return@TextButton
                        // Save the height in SharedPreferences
                        viewModel.saveHeight(convertedHeight)
                        heightDialogOpen.value = false
                    }
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { heightDialogOpen.value = false }) {
                    Text("Cancel")
                }
            },
            text = {
                TextField(
                    value = heightText,
                    onValueChange = {
                        heightText = it
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
        )
    }
}