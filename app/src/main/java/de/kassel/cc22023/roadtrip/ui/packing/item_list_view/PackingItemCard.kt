package de.kassel.cc22023.roadtrip.ui.packing.item_list_view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import de.kassel.cc22023.roadtrip.R
import de.kassel.cc22023.roadtrip.data.repository.database.NotificationType
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItem
import de.kassel.cc22023.roadtrip.ui.packing.PackingViewModel
import de.kassel.cc22023.roadtrip.util.hasNoNotifications

@Composable
fun PackingItemCard (
    item: PackingItem,
    viewModel: PackingViewModel = hiltViewModel()
) {
    val selectedName = remember {
        mutableStateOf(item.name)
    }

    val image: Painter = painterResource(R.drawable.no_image)
    Card(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth() // Card takes full width
    ) {
        Column(
            modifier = Modifier.padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .clickable {
                        item.isChecked = !item.isChecked
                        viewModel.updateItem(item)
                    }
            ) {
                if (item.image != null) {
                    AsyncImage(
                        model = item.image,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                } else {
                    Image(
                        painter = image,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }

                if (item.isChecked) {
                    Image(
                        painter = painterResource(id = R.drawable.selected),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.Center)
                    )
                }

                if (!item.hasNoNotifications())
                    ItemNotificationIcon(item, modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp))
            }

            ItemCardNameField(selectedName, item)
        }
    }
}

@Composable
fun ItemNotificationIcon(item: PackingItem, modifier: Modifier) {
    Box(
        modifier = modifier
            .drawBehind {
                drawCircle(Color.White)
            }
    ) {
        Box(modifier = Modifier.padding(4.dp)) {
            if (!item.hasNoNotifications()) {
                when (item.notificationType) {
                    NotificationType.TIME -> {
                        Image(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = null,
                            contentScale = ContentScale.FillHeight,
                            modifier = Modifier
                                .height(24.dp)
                        )
                    }
                    NotificationType.LOCATION -> {
                        Image(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            contentScale = ContentScale.FillHeight,
                            modifier = Modifier
                                .height(24.dp)
                        )
                    }
                    else -> {
                        val image = painterResource(id = R.drawable.height)
                        Image(
                            painter = image,
                            contentDescription = null,
                            contentScale = ContentScale.FillHeight,
                            modifier = Modifier
                                .height(24.dp),
                            colorFilter = ColorFilter.tint(Color.Black)
                        )
                    }
                }
            }
        }
    }
}