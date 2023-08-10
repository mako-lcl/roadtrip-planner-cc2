package de.kassel.cc22023.roadtrip.ui.packing.item_list_view

import PackingItemDialogSurface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.R
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItem
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripAndLocationsAndList
import de.kassel.cc22023.roadtrip.ui.packing.PackingViewModel
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun PackingListView(
    trip: RoadtripAndLocationsAndList,
    viewModel: PackingViewModel = hiltViewModel(),
) {
    val data = trip.packingItems
    val image: Painter = painterResource(R.drawable.packbg_dark)
    val gridListState = rememberLazyGridState()

    val selectedItem: MutableState<PackingItem?> = remember {
        mutableStateOf(null)
    }

    PackingItemDialogSurface(selectedItem)

    AnimatedVisibility(
        visible = true,
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
                modifier = Modifier
                    .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val reversedList = data.reversed()

                LazyVerticalGrid(columns = GridCells.Fixed(2), state = gridListState) {
                    itemsIndexed(
                        items = reversedList,
                        key = { _, item -> item.hashCode() }) { _, item ->

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
                            PackingItemCard(item, trip.trip.id, selectItem = { selectedItem.value = it })
                        }
                    }
                }
            }
            LaunchedEffect(data) {
                gridListState.scrollToItem(0)
            }
        }
    }
}