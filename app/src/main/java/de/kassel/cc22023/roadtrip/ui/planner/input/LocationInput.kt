package de.kassel.cc22023.roadtrip.ui.planner.input

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.kassel.cc22023.roadtrip.R

@Composable
fun LocationInput(
    startLocation: MutableState<String>,
    startLocationError: MutableState<Boolean>,
    promptText: String
) {
    val context = LocalContext.current

    Row(

        modifier = Modifier
            .padding(horizontal = 32.dp),

    ) {
        TextField(
            shape = RoundedCornerShape(15.dp),
            value = startLocation.value,
            onValueChange = {
                startLocation.value = it
                startLocationError.value = startLocation.value == ""
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ) ,
            label = { Text(text = promptText, maxLines = 1) },
            singleLine = true,
            isError = startLocationError.value,
            supportingText = {
                if (startLocationError.value) {
                    Text(text = context.getString(R.string.location_input_error))
                }
            },
            trailingIcon = {
                if (startLocationError.value) {
                    Icon(
                        painter = painterResource(id = R.drawable.error),
                        contentDescription = "Trailing Icon"
                    )
                }
            }
        )
    }
}