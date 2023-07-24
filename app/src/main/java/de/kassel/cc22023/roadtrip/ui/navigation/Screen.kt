package de.kassel.cc22023.roadtrip.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import de.kassel.cc22023.roadtrip.R

sealed class Screen(val route: String, @StringRes val titleResId: Int, @DrawableRes val iconResId: Int) {
    object Planner : Screen(route = "planner", titleResId= R.string.tab_planner, iconResId = R.drawable.planner)
    object Map : Screen( route = "map", titleResId= R.string.tab_map, iconResId = R.drawable.google_maps)
    object Packing : Screen( route = "packing", titleResId= R.string.tab_packing, iconResId = R.drawable.packing)
}