package de.kassel.cc22023.roadtrip.ui.packing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.data.local.database.NotificationType
import de.kassel.cc22023.roadtrip.data.local.database.PackingItem

@Composable
fun PackingScreen() {

    var packingList by remember { mutableStateOf(PackingItem.exampleData.toMutableList()) }
    var newItemName by remember { mutableStateOf("") }
    var newItemNotificationType by remember { mutableStateOf(NotificationType.NONE) }


        Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Packing list",
            fontSize = 30.sp
        )

        Row {
            Text(modifier = Modifier.weight(0.5f), text = "Item", fontSize = 20.sp)
            Text(modifier = Modifier.weight(0.5f), text = "Notification", fontSize = 20.sp)
        }

        PackingItem.exampleData.forEach {card ->
            PackingItemCard(card)
        }
        PackingItemCard(
            item = PackingItem(0, newItemName, newItemNotificationType, isChecked = false),
            newItem = true,
            newItemName = newItemName,
            newItemNotificationType = newItemNotificationType
        )
        Button(
            onClick = {
                // Add a new PackingItem to the packingList
                val newItem = PackingItem(
                    id = packingList.size + 1,
                    name = newItemName,
                    notificationType = newItemNotificationType,
                    isChecked = false
                )
                packingList.add(newItem)
                packingList = packingList.toMutableList()
                newItemName = "Test"
                newItemNotificationType = NotificationType.NONE
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add New Item")
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackingItemCard(item: PackingItem, newItem: Boolean = false, newItemName: String = "", newItemNotificationType: NotificationType = NotificationType.NONE, viewModel: PackingViewModel = hiltViewModel()) {
    var checked by remember {
        mutableStateOf(item.isChecked)
    }


    var expanded by remember {
        mutableStateOf(false)
    }


    var selectedText by remember {
        mutableStateOf(NotificationType.values().first().value)
    }
    val itemName = if (newItem) newItemName else item.name
    val itemNotificationType = if (newItem) newItemNotificationType else item.notificationType
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(0.5f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = checked, onCheckedChange = {
                checked = it
                item.isChecked = it
                viewModel.updateCheckBoxState(item)

            })

            Text("${item.name}!")
        }

        Row(
            modifier = Modifier.weight(0.5f)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = it
                }
            ) {
                TextField(
                    value = selectedText,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    NotificationType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.value) },
                            onClick = {
                                selectedText = type.value
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }
    }
}



@Preview
@Composable
fun PackingScreenPreview() {
    PackingScreen()
}