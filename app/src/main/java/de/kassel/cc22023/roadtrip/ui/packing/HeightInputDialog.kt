package de.kassel.cc22023.roadtrip.ui.packing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HeightInput(
    onComplete: () -> Unit,
    viewModel: PackingViewModel = hiltViewModel()
) {
    val height by viewModel.height.collectAsState(initial = 0.0)

    var heightText by remember {
        mutableStateOf("")
    }

    LaunchedEffect(height) {
        heightText = height.toString()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = heightText,
            onValueChange = {
                heightText = it
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        TextButton(
            onClick = {
                val convertedHeight = heightText.toDoubleOrNull() ?: return@TextButton
                // Save the height in SharedPreferences
                viewModel.saveHeight(convertedHeight)
                onComplete()
            }
        ) {
            Text("Submit")
        }
    }
}