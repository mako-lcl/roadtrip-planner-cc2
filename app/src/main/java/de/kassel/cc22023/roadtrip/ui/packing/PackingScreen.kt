package de.kassel.cc22023.roadtrip.ui.packing

import HeightPreferences
import PermissionBeforeItemSheet
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mutualmobile.composesensors.rememberPressureSensorState
import de.kassel.cc22023.roadtrip.R
import de.kassel.cc22023.roadtrip.data.local.database.NotificationType
import de.kassel.cc22023.roadtrip.data.local.database.PackingItem
import de.kassel.cc22023.roadtrip.ui.util.LoadingScreen
import de.kassel.cc22023.roadtrip.ui.util.PermissionsRejectedView
import de.kassel.cc22023.roadtrip.util.createNotificationChannel
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import timber.log.Timber
import androidx.compose.material3.Surface
import androidx.compose.ui.text.input.ImeAction


val notificationPermissions = listOf(
    android.Manifest.permission.POST_NOTIFICATIONS,android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PackingScreen(viewModel: PackingViewModel = hiltViewModel()) {
    val context = LocalContext.current

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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackingListScaffold() {
    PackingListView()
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
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
    var isDialogVisible by remember { mutableStateOf(false) }
    var heightValue by remember { mutableStateOf(0f) }
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
    var isContentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isContentVisible = true
    }

    AnimatedVisibility(
        visible = isContentVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {

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

        Row(
            horizontalArrangement = Arrangement.End,
        ) {
            Button(
                onClick = {
                    isDialogVisible = true
                },
            ) {
                Icon(Icons.Default.Settings, contentDescription = null)

            }

            Button(
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
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
        if (isDialogVisible) {
            HeightInputDialog(
                onHeightSubmitted = { height ->
                    heightValue = height
                    isDialogVisible = false
                },
                onDismiss = {
                    isDialogVisible = false
                }
            )
        }

        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

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
                            swipeThreshold = 100.dp,
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
                        TextField(
                            value = selectedName,
                            onValueChange = {newvalue -> selectedName = newvalue
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    val saveName = selectedName
                                    viewModel.saveItem(
                                        item.id,
                                        saveName, // Save the updated text here
                                        item.notificationType,
                                        item.isChecked,
                                        item.time,
                                        item.height,
                                        item.lat,
                                        item.lon,
                                        item
                                    )
                                }),

                            modifier = Modifier
                                .fillMaxHeight(1f),
                            textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
                            colors = TextFieldDefaults.colors(
                                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                disabledTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ))

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
                                    } else {
                                        val newItem = PackingItem(
                                            item.id, item.name, NotificationType.fromString(type.value), item.isChecked, item.time, item.height, item.lat, item.lon
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
@Composable
fun HeightInputDialog(
    onHeightSubmitted: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    var heightText by remember { mutableStateOf(TextFieldValue()) }
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Enter Height") },
        confirmButton = {
            TextButton(
                onClick = {
                    val height = heightText.text.toFloatOrNull()
                    if (height != null) {
                        onHeightSubmitted(height)
                        // Save the height in SharedPreferences
                        HeightPreferences.saveHeight(context, height)}
                    onDismiss()
                }
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
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
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}

@Preview
@Composable
fun PackingScreenPreview() {
    PackingScreen()
}