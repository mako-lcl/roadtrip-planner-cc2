package de.kassel.cc22023.roadtrip.ui.packing

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import de.kassel.cc22023.roadtrip.R
import de.kassel.cc22023.roadtrip.ui.packing.item_list_view.PackingListView
import de.kassel.cc22023.roadtrip.ui.util.LoadingScreen
import de.kassel.cc22023.roadtrip.ui.util.PermissionsRejectedView
import de.kassel.cc22023.roadtrip.util.createNotificationChannel

val notificationPermissions = listOf(
    Manifest.permission.POST_NOTIFICATIONS,
    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PackingScreen(viewModel: PackingViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val image: Painter = painterResource(R.drawable.packbg_dark)
    val permissionState = rememberMultiplePermissionsState(permissions = notificationPermissions)

    if (permissionState.allPermissionsGranted) {
        viewModel.onPermissionGranted()
    } else {
        viewModel.onPermissionDenied()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background image
        Image(
            painter = image,
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.fillMaxSize()
        )
        if (permissionState.shouldShowRationale ||
            !permissionState.allPermissionsGranted ||
            permissionState.revokedPermissions.isNotEmpty()
        ) {
            PermissionsRejectedView()
        } else {
            LaunchedEffect(Unit) {
                createNotificationChannel(context)
            }

            PackingListScreen()
        }
    }
}

@Composable
fun PackingListScreen(
    viewModel: PackingViewModel = hiltViewModel(),
) {
    val data by viewModel.data.collectAsState()

    when (data) {
        is PackingDataUiState.Success -> {
            val trip= (data as PackingDataUiState.Success).data

            PackingListView(trip)
        }

        is PackingDataUiState.NoList -> {
            NoListScreen()
        }

        else -> {
            LoadingScreen()
        }
    }
}

@Preview
@Composable
fun PackingScreenPreview() {
    PackingScreen()
}