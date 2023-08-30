package de.kassel.cc22023.roadtrip.data.sensors

import android.location.Location
import android.os.Looper
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

class SensorRepository(
    private val locationProvider: FusedLocationProviderClient
) {
    val locationFlow = MutableStateFlow<Location?>(null)
    private var hasRequestedLocationUpdates = false

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            locationFlow.value = location
        }
    }

    fun getLocation() : Location? {
        return locationFlow.value
    }


    fun permissionsGranted() {
        if(hasRequestedLocationUpdates) return
        try {
            locationProvider.requestLocationUpdates(
                createLocationRequest(),
                locationCallback,
                Looper.getMainLooper()
            )
            locationProvider.getCurrentLocation(
                CurrentLocationRequest.Builder()
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .setGranularity(Granularity.GRANULARITY_FINE)
                    .build(),
                null
            ).addOnSuccessListener {
                locationFlow.value = it
                hasRequestedLocationUpdates = true
            }
        } catch (e: SecurityException) {
            Timber.e("Cannot fetch location. Missing permissions? $e")
        }
    }

    private fun createLocationRequest() = LocationRequest.Builder(0)
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        .setGranularity(Granularity.GRANULARITY_FINE)
        .setWaitForAccurateLocation(true)
        .build()

    fun permissionDenied() {
        hasRequestedLocationUpdates = false
    }
}