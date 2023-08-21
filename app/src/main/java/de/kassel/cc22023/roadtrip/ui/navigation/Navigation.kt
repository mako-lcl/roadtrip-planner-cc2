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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.kassel.cc22023.roadtrip.ui.map.MapScreen
import de.kassel.cc22023.roadtrip.ui.packing.HeightInputDialog
import de.kassel.cc22023.roadtrip.ui.packing.PackingScreen
import de.kassel.cc22023.roadtrip.ui.packing.PackingTopBar
import de.kassel.cc22023.roadtrip.ui.planner.NewTripDialog
import de.kassel.cc22023.roadtrip.ui.planner.PlannerScreen
import de.kassel.cc22023.roadtrip.ui.planner.PlannerTopBar

enum class TopBarState {
    PLANNER,
    PACKING,
    NONE
}

@SuppressLint("MissingPermission")
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    var topBarState by rememberSaveable { (mutableStateOf(TopBarState.NONE)) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // Control TopBar
    topBarState = when (navBackStackEntry?.destination?.route) {
        Screen.Planner.route -> {
            TopBarState.PLANNER
        }

        Screen.Packing.route -> {
            TopBarState.PACKING
        }

        else -> {
            TopBarState.NONE
        }
    }

    val newTripDialogOpen = remember {
        mutableStateOf(false)
    }
    NewTripDialog(newTripDialogOpen)

    val heightSettingsDialogOpen = remember {
        mutableStateOf(false)
    }
    HeightInputDialog(heightSettingsDialogOpen)

    Scaffold(
        bottomBar = {
            RoadtripNavBar(navController)
        },
        topBar = {
            AnimatedVisibility(
                visible = topBarState != TopBarState.NONE,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                when (topBarState) {
                    TopBarState.PLANNER -> {
                        PlannerTopBar(openDialog = {
                            newTripDialogOpen.value = true
                        })
                    }
                    TopBarState.PACKING -> {
                        PackingTopBar(openSettings = {
                            heightSettingsDialogOpen.value = true
                        })
                    }

                    else -> {}
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