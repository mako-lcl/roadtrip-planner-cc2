package de.kassel.cc22023.roadtrip.ui.planner.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.kassel.cc22023.roadtrip.data.repository.database.TransportationType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportationInput(
    expanded: MutableState<Boolean>,
    selectedText: MutableState<String>
) {
    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = it },
        Modifier.fillMaxWidth()
    ) {
        Surface(
            color = Color.Transparent,
            shadowElevation = 2.dp,
            tonalElevation = 50.dp,
            modifier = Modifier
                .padding(0.dp)
                .fillMaxWidth(1f),
            shape = RoundedCornerShape(15.dp)

        ) {
            TextField(
                shape = RoundedCornerShape(15.dp),
                value = selectedText.value,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
        }
        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
        ) {
            TransportationType.values().forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.value) },
                    onClick = {
                        selectedText.value = type.value
                        expanded.value = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}