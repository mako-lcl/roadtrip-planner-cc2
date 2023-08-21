package de.kassel.cc22023.roadtrip.ui.packing.item_list_view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.sharp.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.R
import de.kassel.cc22023.roadtrip.data.repository.database.NotificationType
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItem
import de.kassel.cc22023.roadtrip.ui.packing.PackingViewModel
import de.kassel.cc22023.roadtrip.util.hasNoNotifications

@Composable
fun PackingItemCard (
    item: PackingItem,
    selectItem: (PackingItem) -> Unit,
    viewModel: PackingViewModel = hiltViewModel()
) {
    val timer: Painter = painterResource(R.drawable.timer)
    val location: Painter = painterResource(R.drawable.location)
    val height: Painter = painterResource(R.drawable.height)

    var isChecked by remember {
        mutableStateOf(item.isChecked)
    }

    val selectedName = remember {
        mutableStateOf(item.name)
    }

    val image: Painter = painterResource(R.drawable.packbg_dark)
    Card(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = image,
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            )
            ItemCardNameField(selectedName, item)

            if (!isChecked) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ItemCardButton(
                        onClick = {
                            item.isChecked = true
                            viewModel.updateItem(item)
                            isChecked = true
                        }, Icons.Filled.CheckCircle, contentDescription = "Check item"
                    )
                    if (item.hasNoNotifications()) {
                        ItemCardButton(
                            onClick = {
                                      selectItem(item)
                            }, Icons.Filled.Notifications, contentDescription = "Notify item"
                        )
                    } else {
                        when (item.notificationType) {
                            NotificationType.TIME -> {
                                Image(
                                    painter = timer,
                                    contentDescription = null,
                                    contentScale = ContentScale.FillHeight,
                                    modifier = Modifier
                                        .height(24.dp)
                                )
                            }
                            NotificationType.LOCATION -> {
                                Image(
                                    painter = location,
                                    contentDescription = null,
                                    contentScale = ContentScale.FillHeight,
                                    modifier = Modifier
                                        .height(24.dp)
                                )
                            }
                            else -> {
                                Image(
                                    painter = height,
                                    contentDescription = null,
                                    contentScale = ContentScale.FillHeight,
                                    modifier = Modifier
                                        .height(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun abc(
    item: PackingItem,
    tripId: Long,
    selectItem: (PackingItem) -> Unit,
    viewModel: PackingViewModel = hiltViewModel()
) {
    var checked by remember {
        mutableStateOf(item.isChecked)
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    val selectedText by remember {
        mutableStateOf(item.notificationType.value)
    }
    var selectedName by remember {
        mutableStateOf(item.name)
    }
    Surface(
        color = Color.Transparent,
        shadowElevation = 1.dp,
        tonalElevation = 10.dp,
        modifier = Modifier
            .padding(1.dp)
            .fillMaxWidth(1f),
        shape = RoundedCornerShape(5.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(50.dp)
                .padding(1.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(0.5f)
                    .height(50.dp)
                    .wrapContentSize(Alignment.TopCenter)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = it
                    }
                ) {
                    Surface(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .padding(0.dp)
                            .fillMaxSize(1f)
                    ) {
                        TextField(
                            value = selectedText,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxHeight(1f),
                            textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
                            colors = TextFieldDefaults.colors(
                                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                disabledTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        NotificationType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.value) },
                                onClick = {
                                    if (type.value != NotificationType.NONE.value) {
                                        val newItem = PackingItem(
                                            item.id,
                                            tripId,
                                            item.name,
                                            NotificationType.fromString(type.value),
                                            item.isChecked,
                                            item.time,
                                            item.height,
                                            item.lat,
                                            item.lon
                                        )
                                        selectItem(newItem)
                                    } else {
                                        val newItem = PackingItem(
                                            item.id,
                                            tripId,
                                            item.name,
                                            NotificationType.fromString(type.value),
                                            item.isChecked,
                                            item.time,
                                            item.height,
                                            item.lat,
                                            item.lon
                                        )
                                        viewModel.updateItem(newItem)
                                    }
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
}