import android.Manifest


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItem
import de.kassel.cc22023.roadtrip.ui.packing.PackingViewModel
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
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large
            ) {
                PackingItemDialog(item = it, closeDialog = {
                    selectedItem.value = null
                })
            }
        }
    }
}

@Composable
fun PackingItemDialog(
    item: PackingItem,
    closeDialog: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Adding padding around the column
        horizontalAlignment = Alignment.CenterHorizontally, // Center horizontally
        verticalArrangement = Arrangement.spacedBy(8.dp) // Add space between elements
    ) {
        Text(item.name)

        when (item.notificationType.value) {
            "Floor" -> {
                FloorInputView(item, closeDialog)
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


