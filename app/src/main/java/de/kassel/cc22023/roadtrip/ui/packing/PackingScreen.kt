package de.kassel.cc22023.roadtrip.ui.packing

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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.shape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import de.kassel.cc22023.roadtrip.ui.theme.darkBackground

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PackingScreen(viewModel: PackingViewModel = hiltViewModel()) {

    val data by viewModel.data.collectAsState()
    var newItemName by remember { mutableStateOf("") }
    var newItemNotificationType by remember { mutableStateOf(NotificationType.NONE) }
    val image: Painter = painterResource(R.drawable.packbg_dark)
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
        Box(modifier = Modifier
            .size(width = 400.dp, height = 100.dp)
            .padding(16.dp)
            .border(width = 2.dp, color = Color(0xFFF4E0B9),shape = RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center){
//        Card(
//            colors = CardDefaults.cardColors(
//            MaterialTheme.colorScheme.primaryContainer
//        ),
//
//            shape = RoundedCornerShape(10.dp),
//            modifier = Modifier
//                .size(width = 200.dp, height = 50.dp)
//                .aspectRatio(2f)
//                .padding(16.dp)
//        ){}

        Text(
            "Pack Your Bags",
            textAlign = TextAlign.Center,
            fontSize = 20.sp, fontFamily = FontFamily.Serif
        )

        }

        Row {
            Box(modifier = Modifier
                .fillMaxSize(0.05f)
                .weight(0.5f)
                .padding(1.dp)
                .border(width = 2.dp, color = Color(0xFFF4E0B9),shape = RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center){

            Text(text = "Carry Me", fontSize = 15.sp)
            }
            Box(modifier = Modifier
                .fillMaxSize(0.05f)
                .weight(0.5f)
                .padding(1.dp)
                .border(width = 2.dp, color = Color(0xFFF4E0B9),shape = RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center){
            Text(text = "Remind Me", fontSize = 15.sp)
        }
        }

        if (data is PackingDataUiState.Success) {
           val list = (data as PackingDataUiState.Success).data
            LazyColumn {
                itemsIndexed(items = list, key = {index,item -> item.hashCode()}) {
                        index,item ->
                    Box(modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                        .padding(1.dp)
                        .border(width = 2.dp, color = Color(0xFFF4E0B9),shape = RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center){
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
            }

        } else {
            LoadingScreen()
        }

        Button(
            onClick = {
                val newItem = PackingItem(
                    id = 0,
                    name = newItemName,
                    notificationType = NotificationType.NONE,
                    isChecked = false
                )
                viewModel.insertIntoList(newItem)
            },
            shape = CircleShape,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add New Item")
        }
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
            .padding(horizontal = 10.dp),
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

            Text("${item.name}!")
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