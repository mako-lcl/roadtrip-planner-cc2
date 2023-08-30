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

package de.kassel.cc22023.roadtrip.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat

//val primaryDark = R.color.brown_700),
//val secondaryDark = R.color.brown_200),

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBA704F),
    secondary = Color(0xFFDFA878),
    tertiary = Color(0xFFF4E0B9),
    primaryContainer = Color(0xFFDFA878),
    onPrimaryContainer = Color(0xFFF4E0B9),

)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFBA704F),
    secondary = Color(0xFFDFA878),
    tertiary = Color(0xFFF4E0B9) ,
    background = LightBlue,
    //surface = darkerBlue,
    onBackground = Color.White,
    //onSurface = Color.White,
    primaryContainer = Color(0xFFDFA878),
    onPrimaryContainer = Color(0xFFF4E0B9)
    /* Other default colors to override



     */
     /*

    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,

    */
)

@Composable
fun RoadtripTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val typography = when {
        darkTheme-> Typography_dark
    else -> Typography}
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
