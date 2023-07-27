package de.kassel.cc22023.roadtrip.ui.packing

import PackingItemSheet
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import de.kassel.cc22023.roadtrip.R
import de.kassel.cc22023.roadtrip.data.local.database.NotificationType
import de.kassel.cc22023.roadtrip.data.local.database.PackingItem
import de.kassel.cc22023.roadtrip.ui.util.LoadingScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.shape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import de.kassel.cc22023.roadtrip.ui.util.PermissionsRejectedView
import de.kassel.cc22023.roadtrip.util.createNotificationChannel
import com.mutualmobile.composesensors.rememberPressureSensorState
import kotlinx.coroutines.launch
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import de.kassel.cc22023.roadtrip.ui.theme.darkBackground

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

            PackingListScaffold()
        }
    } else {
        PackingListScaffold()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackingListScaffold() {
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()

    var selectedItem: PackingItem? by remember {
        mutableStateOf(null)
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetShape = RoundedCornerShape(
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
            topStart = 12.dp,
            topEnd = 12.dp
        ),
        sheetContent = {
            selectedItem?.let { PackingItemSheet(it) }
        },

        sheetPeekHeight = 0.dp
    ) {
        PackingListView {
            coroutineScope.launch {
                selectedItem = it
                bottomSheetScaffoldState.bottomSheetState.expand()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackingListView(
    viewModel: PackingViewModel = hiltViewModel(),
    selectItem: (PackingItem) -> Unit
) {
    val context = LocalContext.current

    var actualHeightText by remember { mutableStateOf("") }
    val data by viewModel.data.collectAsState()
    var newItemName by remember { mutableStateOf("") }
    var newItemNotificationType by remember { mutableStateOf(NotificationType.NONE) }
    val image: Painter = painterResource(R.drawable.packbg_dark)

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
        ) {Button(
            onClick = {
                // Parse the user input to a Double and update the sensoralitude value
                //viewModel.setHeightAndLocation(sensoralitude)
            },

            ) {
            Text("Set Height")
        }
            Box(
                modifier = Modifier
                    .size(width = 400.dp, height = 100.dp)
                    .padding(16.dp)
                    .border(
                        width = 2.dp,
                        color = Color(0xFFF4E0B9),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = if (notificationMessage.isNotBlank()) "Notification: $notificationMessage" else "",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    "Packing list",
                    fontSize = 20.sp
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    label = { Text("Enter new item name") },
                    modifier = Modifier.weight(0.7f)
                )
                Box(
                    modifier = Modifier
                        .weight(0.3f)
                        .padding(horizontal = 8.dp, vertical = 4.dp) // Add padding
                        .height(32.dp) // Set the height of the button
                ){
                    Box{Button(
                        onClick = {
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
                        shape = CircleShape,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add")
                    }}

                }
            }

            Row {
                Box(modifier = Modifier
                    .fillMaxSize(0.1f)
                    .weight(0.5f)
                    .padding(1.dp)
                    .border(width = 2.dp, color = Color(0xFFF4E0B9),shape = RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center){

                    Text(text = "Carry Me", fontSize = 15.sp)
                }
                Box(modifier = Modifier
                    .fillMaxSize(0.1f)
                    .weight(0.5f)
                    .padding(1.dp)
                    .border(width = 2.dp, color = Color(0xFFF4E0B9),shape = RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center){
                    Text(text = "Remind Me", fontSize = 15.sp)
                }
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
                        }, dismissContent = { PackingItemCard(item) {
                            selectItem(it)
                        } })
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
            DismissValue.DismissedToEnd -> Color(0xFFDFA878)
            DismissValue.DismissedToStart -> Color(0xFFDFA878)
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
    viewModel: PackingViewModel = hiltViewModel(),
    selectItem: (PackingItem) -> Unit
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
                viewModel.updateItem(item)

            })

            Text(item.name,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFDFA878)
            )

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
                                if (type.value != NotificationType.NONE.value) {
                                    val newItem = PackingItem(
                                        item.id, item.name, NotificationType.fromString(selectedText), item.isChecked, item.time, item.height, item.lat, item.lon
                                    )
                                    selectItem(newItem)
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


@Preview
@Composable
fun PackingScreenPreview() {
    PackingScreen()
}