package de.kassel.cc22023.roadtrip.ui.packing

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import android.content.Context
import PackingItemSheet
import PermissionBeforeItemSheet
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
import androidx.compose.material3.*
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import de.kassel.cc22023.roadtrip.ui.util.PermissionsRejectedView
import de.kassel.cc22023.roadtrip.util.createNotificationChannel
import com.mutualmobile.composesensors.rememberPressureSensorState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.style.TextAlign
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
val notificationPermissions = listOf(
    android.Manifest.permission.POST_NOTIFICATIONS,android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
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
    val bottomSheetState: SheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Hidden,
        skipHiddenState = false
    )
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = bottomSheetState
    )

    PackingListView()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackingListView(
    viewModel: PackingViewModel = hiltViewModel(),
) {
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

    val listState = rememberLazyListState()
    val pressureState = rememberPressureSensorState()
    val notificationMessage by remember { mutableStateOf("") }
    sensoralitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressureState.pressure)
    val height by remember { mutableStateOf(0.0f) }

    var selectedItem: PackingItem? by remember {
        mutableStateOf(null)
    }

    selectedItem?.let {
        Dialog(onDismissRequest = {}) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large
            ) {
                PermissionBeforeItemSheet(item = it, closeDialog = {
                    selectedItem = null
                })
            }
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row{
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


            Surface(
                color = Color.Transparent,
                shadowElevation = 3.dp,
                tonalElevation = 10.dp,
                modifier = Modifier
                    .padding(1.dp)
                    .fillMaxWidth(0.8f),
                shape = RoundedCornerShape(5.dp)
            ) {
            Box(modifier = Modifier
                .size(width = 200.dp, height = 50.dp)
                .padding(5.dp),
                contentAlignment = Alignment.Center
            )
            {

            Text(
                "Pack Your Bags",
                fontSize = 30.sp, fontFamily = FontFamily.Serif
            )
            }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Text input field to enter the new item name
                Box(modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .padding(1.dp)
                    .border(
                        width = 0.dp,
                        color = Color(0xFFF4E0B9),
                        shape = RoundedCornerShape(20.dp)
                    ),
                    contentAlignment = Alignment.Center){
                TextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    label = { Text("Enter new item name") },
//                    modifier = Modifier.weight(0.7f),
                    shape = RoundedCornerShape(20.dp),


                )
            }
            }
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
                                0.0,
                                0.0,
                                0.0
                            )
                            viewModel.insertIntoList(newItem)
                            newItemName = ""
                            newItemNotificationType = NotificationType.NONE

                        },
                        shape = CircleShape,
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Text("Add")
                    }}

                }
            Row {
                Surface(
                    color = Color(0xFFDFA878),
                    shadowElevation = 5.dp,
                    tonalElevation = 10.dp,
                    modifier = Modifier
                        .padding(1.dp)
                        .fillMaxWidth(0.5f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                Box(modifier = Modifier
                    .fillMaxSize(0.1f)
                    .weight(0.5f)
                    .padding(1.dp),
                    contentAlignment = Alignment.Center){

                    Text(text = "Carry Me", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Serif)
                }
                }
                Surface(
                    color = Color(0xFFDFA878),
                    shadowElevation = 5 .dp,
                    tonalElevation = 10.dp,
                    modifier = Modifier
                        .padding(1.dp)
                        .fillMaxWidth(1f),
                    shape = RoundedCornerShape(4.dp)

                ) {
                Box(modifier = Modifier
                    .fillMaxSize(0.1f)
                    .weight(0.5f)
                    .padding(1.dp),

                    contentAlignment = Alignment.Center){
                    Text(text = "Remind Me At", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Serif)
                }
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

                        val delete = SwipeAction(
                            onSwipe = {
                                viewModel.onSwipeToDelete(item)
                            },
                            icon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete chat",
                                    modifier = Modifier.padding(16.dp),
                                    tint = Color.White
                                )
                            }, background = Color.Red.copy(alpha = 0.5f),
                            isUndo = true
                        )
                        SwipeableActionsBox(
                            modifier = Modifier,
                            swipeThreshold = 200.dp,
                            endActions = listOf(delete)
                        ) {
                            PackingItemCard(item, ){selectedItem = it}
                        }
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

    val selectedText by remember {
        mutableStateOf(item.notificationType.value)
    }
    Surface(
        color = Color.Transparent,
        shadowElevation = 1.dp,
        tonalElevation = 10.dp,
        modifier = Modifier
            .padding(1.dp)
            .fillMaxWidth(1f),
        shape = RoundedCornerShape(5    .dp)

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
                .wrapContentSize(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = checked, onCheckedChange = {
                checked = it
                item.isChecked = it
                viewModel.updateItem(item)

            })
            Surface(
                color = Color(0xFFCDC2AE),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier
                    .padding(0.dp)
                    .fillMaxSize(1f),
            ) {
                Box(contentAlignment = Alignment.Center){
            Text(item.name, color = Color(0xFFBA704F), textAlign = TextAlign.Center, fontWeight = FontWeight.Medium )
        }
            }
        }
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
                                        item.id, item.name, NotificationType.fromString(type.value), item.isChecked, item.time, item.height, item.lat, item.lon
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
}

@Preview
@Composable
fun PackingScreenPreview() {
    PackingScreen()
}