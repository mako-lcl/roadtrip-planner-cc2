package de.kassel.cc22023.roadtrip.ui.planner.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.kassel.cc22023.roadtrip.R
import de.kassel.cc22023.roadtrip.ui.util.DatePickerDialog
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateInput(
    date: MutableState<String>,
    showDatePicker: MutableState<Boolean>,
    dateError: MutableState<Boolean>,
    selectedDate: MutableState<LocalDate>,
    startDateError: MutableState<Boolean>,
    selectedStartDate: MutableState<LocalDate>,
    endDateError: MutableState<Boolean>,
    selectedEndDate: MutableState<LocalDate>,
    promptText: String
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .padding(horizontal = 32.dp)
    ) {


            TextField(
                shape = RoundedCornerShape(15.dp),
                value = date.value,
                onValueChange = {},
                singleLine = true,
                enabled = false,
                modifier = Modifier.clickable(onClick = {
                    showDatePicker.value = true
                }),
                label = { Text(text = promptText) },
                colors = TextFieldDefaults.colors(

                    disabledContainerColor = if (!dateError.value) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                    disabledTextColor = if (!dateError.value) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.error,
                    disabledTrailingIconColor = if (!dateError.value) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.error,
                    disabledSupportingTextColor = MaterialTheme.colorScheme.error,
                    //errorContainerColor = colorScheme.error,
                    errorTextColor = MaterialTheme.colorScheme.error,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent


                ),
                trailingIcon = {
                    if (!dateError.value) {
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
                    if (dateError.value) {
                        Text(text = context.getString(R.string.date_input_error))
                    }
                },
                isError = dateError.value,
            )



        if (showDatePicker.value) {
            DatePickerDialog(
                state = rememberDatePickerState(
                    initialSelectedDateMillis = selectedDate.value.toEpochDay() * 24 * 60 * 60 * 1000
                ),
                onDateSelected = { newDate ->
                    selectedDate.value =
                        Instant.ofEpochMilli(newDate).atZone(ZoneOffset.UTC).toLocalDate()
                    date.value = selectedDate.value.toString()
                    showDatePicker.value = false
                    startDateError.value = selectedStartDate.value.isBefore(LocalDate.now())
                    endDateError.value =
                        selectedEndDate.value.isBefore(selectedStartDate.value.plusDays(1))
                },
                onCloseDialog = { showDatePicker.value = false }
            )
        }
    }
}