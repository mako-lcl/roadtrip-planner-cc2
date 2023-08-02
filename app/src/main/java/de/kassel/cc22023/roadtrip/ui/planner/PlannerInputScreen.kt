package de.kassel.cc22023.roadtrip.ui.planner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.R
import de.kassel.cc22023.roadtrip.data.repository.database.TransportationType
import de.kassel.cc22023.roadtrip.ui.util.DatePickerDialog
import de.kassel.cc22023.roadtrip.util.convertRoadtripFromTestTrip
import de.kassel.cc22023.roadtrip.util.loadRoadtripFromAssets
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterial3Api
@Composable
fun PlannerInputScreen(
    viewModel: PlannerViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var selectedStartDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedEndDate by remember { mutableStateOf(LocalDate.now().plusDays(1)) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var startLocation by remember { mutableStateOf("") }
    var endLocation by remember { mutableStateOf("") }
    var expanded by remember {
        mutableStateOf(false)
    }
    var selectedText by remember {
        mutableStateOf(TransportationType.values().first().value)
    }
    var startDate by remember { mutableStateOf(selectedStartDate.toString()) }
    var endDate by remember { mutableStateOf(selectedEndDate.toString()) }
    var startDateError by remember { mutableStateOf(false) }
    var endDateError by remember { mutableStateOf(false) }
    var endLocationError by remember { mutableStateOf(false) }
    var startLocationError by remember { mutableStateOf(false) }
    val currentDate = remember { LocalDate.now() }
    val image: Painter = painterResource(R.drawable.map_pin)

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

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(50.dp))
                AnimatedVisibility(
                    visible = isContentVisible,
                    enter = scaleIn(
                        animationSpec = tween(500),
                        transformOrigin = TransformOrigin(0f, 0f)
                    )
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
                        Box(
                            modifier = Modifier
                                .size(width = 200.dp, height = 100.dp)
                                .padding(5.dp)
                                .animateContentSize(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Roadtrip Planner",
                                fontSize = 25.sp,
                                fontFamily = FontFamily.Serif,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))

                // First Date Selection

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(0.dp)
                ) {
                    Surface(
                        color = Color.Transparent,
                        shadowElevation = 1.dp,
                        tonalElevation = 50.dp,
                        modifier = Modifier
                            .padding(0.dp)
                            .fillMaxWidth(0.5f),
                        shape = RoundedCornerShape(15.dp)

                    ) {
                        TextField(
                            shape = RoundedCornerShape(15.dp),
                            value = startLocation,
                            onValueChange = {
                                startLocation = it
                                startLocationError = startLocation == ""
                            },
                            label = { Text(text = "Start", maxLines = 1) },

                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            isError = startLocationError,
                            supportingText = {
                                if (startLocationError) {
                                    Text(text = "Can not be empty")
                                }
                            },
                            trailingIcon = {
                                if (startLocationError) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.error),
                                        contentDescription = "Trailing Icon"
                                    )
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            color = Color.Transparent,
                            shadowElevation = 1.dp,
                            tonalElevation = 50.dp,
                            modifier = Modifier
                                .padding(0.dp)
                                .fillMaxWidth(1f),
                            shape = RoundedCornerShape(15.dp)

                        ) {
                            TextField(
                                shape = RoundedCornerShape(15.dp),
                                value = startDate,
                                onValueChange = {},
                                singleLine = true,
                                enabled = false,
                                modifier = Modifier.clickable(onClick = {
                                    showStartDatePicker = true
                                }),
                                label = { Text(text = "Start Date") },
                                colors = TextFieldDefaults.colors(

                                    disabledContainerColor = if (!startDateError) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                                    disabledTextColor = if (!startDateError) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.error,
                                    disabledTrailingIconColor = if (!startDateError) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.error,
                                    disabledSupportingTextColor = MaterialTheme.colorScheme.error,
                                    //errorContainerColor = colorScheme.error,
                                    errorTextColor = MaterialTheme.colorScheme.error


                                ),
                                trailingIcon = {
                                    if (!startDateError) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                                            contentDescription = "Trailing Icon"
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(id = R.drawable.error),
                                            contentDescription = "Trailing Icon"
                                        )
                                    }
                                },
                                supportingText = {
                                    if (startDateError) {
                                        Text(text = "Can not be in the past")
                                    }
                                },
                                isError = startDateError,
                            )
                        }
                    }
                }

                if (showStartDatePicker) {
                    DatePickerDialog(
                        state = rememberDatePickerState(
                            initialSelectedDateMillis = selectedStartDate.toEpochDay() * 24 * 60 * 60 * 1000
                        ),
                        onDateSelected = { newDate ->
                            selectedStartDate =
                                Instant.ofEpochMilli(newDate).atZone(ZoneOffset.UTC).toLocalDate()
                            startDate = selectedStartDate.toString()
                            showStartDatePicker = false
                            startDateError = selectedStartDate.isBefore(currentDate)
                            endDateError = selectedEndDate.isBefore(selectedStartDate.plusDays(1))
                        },
                        onCloseDialog = { showStartDatePicker = false }
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))

                // Second Date Selection


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        color = Color.Transparent,
                        shadowElevation = 1.dp,
                        tonalElevation = 50.dp,
                        modifier = Modifier
                            .padding(0.dp)
                            .fillMaxWidth(0.5f),
                        shape = RoundedCornerShape(15.dp)

                    ) {
                        TextField(
                            shape = RoundedCornerShape(15.dp),
                            value = endLocation,
                            onValueChange = {
                                endLocation = it
                                endLocationError = endLocation == ""
                            },
                            label = { Text("Destination") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            isError = endLocationError,
                            supportingText = {
                                if (endLocationError) {
                                    Text(text = "Can not be empty")
                                }
                            },
                            trailingIcon = {
                                if (endLocationError) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.error),
                                        contentDescription = "Trailing Icon"
                                    )
                                }
                            }

                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            color = Color.Transparent,
                            shadowElevation = 1.dp,
                            tonalElevation = 50.dp,
                            modifier = Modifier
                                .padding(0.dp)
                                .fillMaxWidth(1f),
                            shape = RoundedCornerShape(15.dp)

                        ) {
                            TextField(
                                shape = RoundedCornerShape(15.dp),
                                value = endDate,
                                onValueChange = {},
                                singleLine = true,
                                enabled = false,
                                modifier = Modifier.clickable(onClick = {
                                    showEndDatePicker = true
                                }),
                                label = { Text(text = "End Date") },
                                colors = TextFieldDefaults.colors(

                                    disabledContainerColor = if (!endDateError) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                                    disabledTextColor = if (!endDateError) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.error,
                                    disabledTrailingIconColor = if (!endDateError) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.error,
                                    disabledSupportingTextColor = MaterialTheme.colorScheme.error,
                                    // errorContainerColor = colorScheme.error,
                                    errorTextColor = MaterialTheme.colorScheme.error


                                ),
                                trailingIcon = {
                                    if (!endDateError) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                                            contentDescription = "Trailing Icon"
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(id = R.drawable.error),
                                            contentDescription = "Trailing Icon"
                                        )
                                    }
                                },
                                supportingText = {
                                    if (endDateError) {
                                        Text(text = "Has to be after the Start Date")
                                    }
                                },
                                isError = endDateError,


                                )
                        }
                    }
                }

                if (showEndDatePicker) {
                    DatePickerDialog(
                        state = rememberDatePickerState(
                            initialSelectedDateMillis = selectedEndDate.toEpochDay() * 24 * 60 * 60 * 1000
                        ),
                        onDateSelected = { newDate ->
                            selectedEndDate =
                                Instant.ofEpochMilli(newDate).atZone(ZoneOffset.UTC).toLocalDate()
                            endDate = selectedEndDate.toString()
                            showEndDatePicker = false
                            endDateError = selectedEndDate.isBefore(selectedStartDate.plusDays(1))
                        },

                        onCloseDialog = { showEndDatePicker = false }

                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
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
                            value = selectedText,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                    }
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        TransportationType.values().forEach { type ->
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
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = {
                    startLocationError = startLocation == ""
                    endLocationError = endLocation == ""
                    if (!startDateError && !endDateError && !startLocationError && !endLocationError) {
                        viewModel.createRoadtrip(
                            startDate,
                            endDate,
                            startLocation,
                            endLocation,
                            selectedText
                        )
                    }
                }) {
                    Text("Load from GPT")
                }

                Button(onClick = {
                    val testTrip = loadRoadtripFromAssets(context)
                    val trip = convertRoadtripFromTestTrip(testTrip)
                    viewModel.insertNewRoadtrip(trip)
                }) {
                    Text(text = "Load from Disk")
                }
            }

        }

    }
}