/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.kassel.cc22023.roadtrip.ui.navigation

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.kassel.cc22023.roadtrip.ui.map.MapScreen
import de.kassel.cc22023.roadtrip.ui.packing.NewItemDialog
import de.kassel.cc22023.roadtrip.ui.packing.PackingScreen
import de.kassel.cc22023.roadtrip.ui.packing.PackingViewModel
import de.kassel.cc22023.roadtrip.ui.planner.NewTripDialog
import de.kassel.cc22023.roadtrip.ui.planner.PlannerScreen

@SuppressLint("MissingPermission")
@Composable
fun MainNavigation(
    packingViewModel: PackingViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val currentTrip by packingViewModel.currentTripId.collectAsState(initial = -1L)
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val newTripDialogOpen = remember {
        mutableStateOf(false)
    }
    NewTripDialog(newTripDialogOpen)

    val newItemDialog = remember {
        mutableStateOf(false)
    }
    NewItemDialog(currentTrip, newItemDialog)

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            RoadtripNavBar(navController)
        },
        floatingActionButton = {
            when (navBackStackEntry?.destination?.route) {
                Screen.Planner.route -> {
                    FloatingActionButton(containerColor = MaterialTheme.colorScheme.primary,onClick = { newTripDialogOpen.value = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add new trip"
                        )
                    }
                }

                Screen.Packing.route -> {
                    FloatingActionButton(containerColor = MaterialTheme.colorScheme.primary,onClick = {
                        if (currentTrip != -1L) {
                            newItemDialog.value = true
                        } else {
                            Toast
                                .makeText(context, "Error while creating item", Toast.LENGTH_LONG)
                                .show()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add new trip"
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Planner.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Planner.route) { PlannerScreen(onNavigateToMap = { navigateToRoute(Screen.Map.route, navController) }) }
            composable(Screen.Map.route) { MapScreen() }
            composable(Screen.Packing.route) { PackingScreen() }
        }
    }
}

fun navigateToRoute(route: String, navController: NavController) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}