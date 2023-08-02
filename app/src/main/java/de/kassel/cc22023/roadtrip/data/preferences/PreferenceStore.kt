package de.kassel.cc22023.roadtrip.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import de.kassel.cc22023.roadtrip.data.preferences.PreferencesKeys.CURRENT_TRIP
import de.kassel.cc22023.roadtrip.data.preferences.PreferencesKeys.KEY_HEIGHT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val PREFS_NAME = "Height_Prefs"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFS_NAME)

object PreferencesKeys {
    val KEY_HEIGHT = doublePreferencesKey("height")
    val CURRENT_TRIP = intPreferencesKey("currentTrip")
}

class PreferenceStore(
    private val context: Context
) {
    val height: Flow<Double> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_HEIGHT] ?: 0.0
        }

    val currentTrip: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[CURRENT_TRIP] ?: -1
        }

    suspend fun setHeight(height: Double) {
        context.dataStore.edit {
            it[KEY_HEIGHT] = height
        }
    }

    suspend fun setCurrentTrip(trip: Int) {
        context.dataStore.edit {
            it[CURRENT_TRIP] = trip
        }
    }
}