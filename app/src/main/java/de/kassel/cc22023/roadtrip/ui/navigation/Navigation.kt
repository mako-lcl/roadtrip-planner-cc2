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

package de.kassel.cc22023.roadtrip.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.kassel.cc22023.roadtrip.ui.map.MapScreen
import de.kassel.cc22023.roadtrip.ui.navigation.Screen
import de.kassel.cc22023.roadtrip.ui.packing.PackingScreen
import de.kassel.cc22023.roadtrip.ui.planner.NewTripDialog
import de.kassel.cc22023.roadtrip.ui.planner.PlannerScreen
import de.kassel.cc22023.roadtrip.ui.planner.PlannerTopBar

@SuppressLint("MissingPermission")
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    var topBarState by rememberSaveable { (mutableStateOf(true)) }

    val newTripDialogOpen = remember {
        mutableStateOf(false)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // Control TopBar
    topBarState = when (navBackStackEntry?.destination?.route) {
        Screen.Planner.route -> {
            true
        }

        else -> {
            false
        }
    }

    NewTripDialog(newTripDialogOpen)

    Scaffold(
        bottomBar = {
            RoadtripNavBar(navController)
        },
        topBar = {
            AnimatedVisibility(
                visible = topBarState,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                PlannerTopBar {
                    newTripDialogOpen.value = true
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Planner.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Planner.route) { PlannerScreen() }
            composable(Screen.Map.route) { MapScreen() }
            composable(Screen.Packing.route) { PackingScreen() }
        }
    }
}

@Composable
fun RoadtripNavBar(navController: NavController) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier
            .size(width = 400.dp, height = 50.dp)
            .background(color = Color.Transparent)
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(0.1f)
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            listOf(
                Screen.Planner,
                Screen.Map,
                Screen.Packing,
            ).forEach { screen ->
                val isSelected =
                    currentDestination?.hierarchy?.any { it.route == screen.route } == true
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = screen.iconResId),
                            contentDescription = null,
                            tint = if (isSelected) Color(0xFFDFA878) else Color(0xFFF4E0B9)
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    }

                )
            }
        }
    }
}