package de.kassel.cc22023.roadtrip.ui.packing

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.kassel.cc22023.roadtrip.R

@Composable
fun ChooseNotificationTypeDialog(
    chooseTimer: () -> Unit,
    chooseLocation: () -> Unit,
    chooseFloor: () -> Unit
) {
    val timer: Painter = painterResource(R.drawable.timer)
    val location: Painter = painterResource(R.drawable.location)
    val height: Painter = painterResource(R.drawable.height)
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Choose notification type")
        Card(
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    chooseTimer()
                },
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = timer,
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .height(120.dp)
                )
                Text("Time")
            }
        }
        Card(
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    chooseLocation()
                },
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = location,
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .height(120.dp)
                )
                Text("Location")
            }
        }
        Card(
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    chooseFloor()
                },
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = height,
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .height(120.dp)
                )
                Text("Floor")
            }
        }
    }
}

@Preview
@Composable
fun ChooseNotificationTypeDialogPreview() {
    ChooseNotificationTypeDialog(
        chooseFloor = {},
        chooseLocation = {},
        chooseTimer = {},
    )
}