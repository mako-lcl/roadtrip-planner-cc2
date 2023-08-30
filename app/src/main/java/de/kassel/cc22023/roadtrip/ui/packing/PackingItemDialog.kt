import android.Manifest
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import de.kassel.cc22023.roadtrip.data.repository.database.NotificationType
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItem
import de.kassel.cc22023.roadtrip.ui.packing.ChooseNotificationTypeDialog
import de.kassel.cc22023.roadtrip.ui.packing.HeightInput
import de.kassel.cc22023.roadtrip.ui.packing.reminder_dialog.FloorInputView
import de.kassel.cc22023.roadtrip.ui.packing.reminder_dialog.LocationInputView
import de.kassel.cc22023.roadtrip.ui.packing.reminder_dialog.TimeInputView
import de.kassel.cc22023.roadtrip.util.PermissionBox


@Composable
fun PackingItemDialogSurface(
    selectedItem: MutableState<PackingItem?>
) {
    selectedItem.value?.let {
        Dialog(onDismissRequest = { selectedItem.value = null }) {
            Surface(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(.95f),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PackingItemDialog(item = it, closeDialog = {
                        selectedItem.value = null
                    })
                }
            }
        }
    }
}

enum class PackingItemDialogState {
    CHOOSING_TYPE,
    HEIGHT,
    INPUT
}

@Composable
fun PackingItemDialog(
    item: PackingItem,
    closeDialog: () -> Unit,
) {
    var state by remember {
        mutableStateOf(PackingItemDialogState.CHOOSING_TYPE)
    }

    var notificationType by remember {
        mutableStateOf(NotificationType.NONE)
    }

    Crossfade(targetState = state, animationSpec = tween(500)) {screen ->
        if (screen == PackingItemDialogState.CHOOSING_TYPE) {
            ChooseNotificationTypeDialog(
                chooseTimer = {
                    notificationType = NotificationType.TIME
                    state = PackingItemDialogState.INPUT
                },
                chooseLocation = { 
                    notificationType = NotificationType.LOCATION
                    state = PackingItemDialogState.INPUT
                },
                chooseFloor = {
                    notificationType = NotificationType.FLOOR
                    state = PackingItemDialogState.INPUT
                })
        } else if (screen == PackingItemDialogState.HEIGHT) {
            HeightInput(onComplete = {
                state = PackingItemDialogState.INPUT
            })
        } else {
            PackingNotificationDialog(item = item, notificationType, closeDialog = closeDialog, onSettings = {
                state = PackingItemDialogState.HEIGHT
            })
        }
    }
}

@Composable
fun PackingNotificationDialog(
    item: PackingItem,
    notificationType: NotificationType,
    closeDialog: () -> Unit,
    onSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Adding padding around the column
        horizontalAlignment = Alignment.CenterHorizontally, // Center horizontally
        verticalArrangement = Arrangement.spacedBy(8.dp) // Add space between elements
    ) {
        when (notificationType.value) {
            "Floor" -> {
                FloorInputView(item, closeDialog = closeDialog, onSettings = onSettings)
            }

            "Location" -> {
                LocationInputView(item, closeDialog)
            }

            "Time" -> {
                val permissions = listOf(
                    Manifest.permission.SCHEDULE_EXACT_ALARM,
                )

                PermissionBox(
                    permissions = permissions,
                    requiredPermissions = listOf(permissions.first()),
                    description = "App needs permission to set alarm",
                    onGranted = {
                        TimeInputView(item, closeDialog)
                    },
                )
            }
        }
    }
}


