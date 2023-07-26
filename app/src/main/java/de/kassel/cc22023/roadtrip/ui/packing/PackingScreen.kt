package de.kassel.cc22023.roadtrip.ui.packing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import de.kassel.cc22023.roadtrip.R
import de.kassel.cc22023.roadtrip.data.local.database.NotificationType
import de.kassel.cc22023.roadtrip.data.local.database.PackingItem
import de.kassel.cc22023.roadtrip.ui.util.LoadingScreen
import timber.log.Timber
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import de.kassel.cc22023.roadtrip.ui.util.PermissionsRejectedView
import de.kassel.cc22023.roadtrip.util.createNotificationChannel
import de.kassel.cc22023.roadtrip.util.sendNotificationWithRuntime
import com.mutualmobile.composesensors.rememberPressureSensorState
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
val notificationPermissions = listOf(
    android.Manifest.permission.POST_NOTIFICATIONS,
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PackingScreen(viewModel: PackingViewModel = hiltViewModel()) {
    val context = LocalContext.current

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionState = rememberMultiplePermissionsState(permissions = notificationPermissions)

        if (permissionState.allPermissionsGranted) {
            viewModel.onPermissionGranted()
        } else {
            viewModel.onPermissionDenied()
        }

        if (permissionState.shouldShowRationale ||
            !permissionState.allPermissionsGranted ||
            permissionState.revokedPermissions.isNotEmpty()
        ) {
            PermissionsRejectedView()
        } else {
            LaunchedEffect(Unit) {
                createNotificationChannel(context)
            }

            PackingListView()
        }
    } else {
        PackingListView()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackingListView(
    viewModel: PackingViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var actualHeightText by remember { mutableStateOf("") }
    val data by viewModel.data.collectAsState()
    var newItemName by remember { mutableStateOf("") }
    var newItemNotificationType by remember { mutableStateOf(NotificationType.NONE) }
    val image: Painter = painterResource(R.drawable.packbg)
    var expanded by remember {
        mutableStateOf(false)
    }
    var sensoralitude by remember {mutableStateOf ( SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE,
        Sensor.TYPE_PRESSURE.toFloat()
    ))}

    var selectedText by remember {
        mutableStateOf(NotificationType.values().first().value)
    }
    val listState = rememberLazyListState()
    val pressureState = rememberPressureSensorState()
    val notificationMessage by remember { mutableStateOf("") }
    sensoralitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressureState.pressure)
    val height by remember { mutableStateOf(0.0f) }

    var showSheet by remember { mutableStateOf(false) }

    if (showSheet) {
        PackingItemSheet {
            showSheet = false
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Background image
        Image(
            painter = image,
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.fillMaxSize()
        )




        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row {
                // Text input field to enter the new item name

            }
            Button(
                onClick = {
                    // Parse the user input to a Double and update the sensoralitude value
                    //viewModel.setHeightAndLocation(sensoralitude)
                },

            ) {
                Text("Set Height")
            }
            Text(
                text = "Sensor Altitude: $height m",
                fontSize = 18.sp,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = if (notificationMessage.isNotBlank()) "Notification: $notificationMessage" else "",
                fontSize = 18.sp,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                "Packing list",
                fontSize = 30.sp
            )

            Row {
                // Text input field to enter the new item name
                TextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    label = { Text("Enter new item name") },
                    modifier = Modifier.weight(0.7f)
                )
            }
            Row {
                // Dropdown menu to select the notification type for the new item
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.weight(0.3f)
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
                                    newItemNotificationType =
                                        type // Update the newItemNotificationType when a value is selected
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    showSheet = true

                    // Add a new PackingItem to the packingList
                    val newItem = PackingItem(
                        id = 0,
                        name = newItemName,
                        notificationType = newItemNotificationType,
                        isChecked = false,
                        null,
                        0f,
                        0f,
                        0f
                    )
                    viewModel.insertIntoList(newItem)
                    newItemName = ""
                    newItemNotificationType = NotificationType.NONE

                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add New Item")
            }

            Row {
                Text(modifier = Modifier.weight(0.5f), text = "Item", fontSize = 20.sp)
                Text(modifier = Modifier.weight(0.5f), text = "Notification", fontSize = 20.sp)
            }
            if (data is PackingDataUiState.Success) {
                val list = (data as PackingDataUiState.Success).data
                val reversedList = list.reversed()

                LazyColumn(state = listState) {
                    itemsIndexed(
                        items = reversedList,
                        key = { index, item -> item.hashCode() }) { index, item ->
                        val currentItem by rememberUpdatedState(newValue = item)

                        val dismissState = rememberDismissState(confirmValueChange = {
                            viewModel.deleteItem(currentItem)
                            true
                        })
                        SwipeToDismiss(state = dismissState, background = {
                            SwipeBackground(dismissState)
                        }, dismissContent = { PackingItemCard(item) })
                    }
                }
            } else {
                LoadingScreen()
            }


        }
        LaunchedEffect(key1 = viewModel.data.collectAsState().value) {
            if (data is PackingDataUiState.Success) {
                listState.scrollToItem(0)
            }
        }
        LaunchedEffect(sensoralitude) {
            //notificationMessage = viewModel.getNotificationMessage(sensoralitude)
            //TODO
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeBackground(dismissState: DismissState) {
    val direction = dismissState.dismissDirection ?: return

    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> Color.LightGray
            DismissValue.DismissedToEnd -> Color.Red
            DismissValue.DismissedToStart -> Color.Red
        }
    )
    val alignment = when (direction) {
        DismissDirection.StartToEnd -> Alignment.CenterStart
        DismissDirection.EndToStart -> Alignment.CenterEnd
    }
    val icon = when (direction) {
        DismissDirection.StartToEnd -> Icons.Default.Delete
        DismissDirection.EndToStart -> Icons.Default.Delete
    }
    val scale by animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 20.dp),
        contentAlignment = alignment
    ) {
        Icon(
            icon,
            contentDescription = "Localized description",
            modifier = Modifier.scale(scale)
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackingItemCard(
    item: PackingItem,
    viewModel: PackingViewModel = hiltViewModel()
) {
    var checked by remember {
        mutableStateOf(item.isChecked)
    }


    var expanded by remember {
        mutableStateOf(false)
    }


    var selectedText by remember {
        mutableStateOf(NotificationType.values().first().value)
    }
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

            Text(item.name)
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