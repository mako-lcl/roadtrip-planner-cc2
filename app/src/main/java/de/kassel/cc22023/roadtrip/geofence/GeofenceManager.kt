package de.kassel.cc22023.roadtrip.geofence


import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import timber.log.Timber

const val CUSTOM_REQUEST_CODE_GEOFENCE = 3232
const val CUSTOM_INTENT_GEOFENCE = "CUSTOM_INTENT_GEOFENCE"

class GeofenceManager(context: Context) {
    private val TAG = "GeofenceManager"
    private val client = LocationServices.getGeofencingClient(context)
    private val geofenceList = mutableMapOf<Long, Geofence>()

    private val geofencingPendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(context, CUSTOM_REQUEST_CODE_GEOFENCE, intent, PendingIntent.FLAG_MUTABLE)
    }

    fun clearGeofences() =
        geofenceList.clear()

    fun addGeofence(
        key: Long,
        location: Location,
        radiusInMeters: Float = 100.0f,
        expirationTimeInMillis: Long = 24 * 60 * 60 * 1000,
    ) {
        geofenceList[key] = createGeofence(key, location, radiusInMeters, expirationTimeInMillis)
    }

    @SuppressLint("MissingPermission")
    fun registerGeofence() {
        client.addGeofences(createGeofencingRequest(), geofencingPendingIntent)
            .addOnSuccessListener {
                Timber.tag(TAG).d("registerGeofence: SUCCESS")
            }.addOnFailureListener { exception ->
                Timber.tag(TAG).d("registerGeofence: Failure\n$exception")
            }
    }

    suspend fun deregisterGeofenceAndReregisterFences() = kotlin.runCatching {
        client.removeGeofences(geofencingPendingIntent).await()
        registerGeofence()
    }

    suspend fun deregisterGeofence() = kotlin.runCatching {
        client.removeGeofences(geofencingPendingIntent).await()
    }

    private fun createGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GEOFENCE_TRANSITION_ENTER)
            addGeofences(geofenceList.values.toList())
        }.build()
    }

    private fun createGeofence(
        key: Long,
        location: Location,
        radiusInMeters: Float,
        expirationTimeInMillis: Long,
    ): Geofence {
        return Geofence.Builder()
            .setRequestId(key.toString())
            .setCircularRegion(location.latitude, location.longitude, radiusInMeters)
            .setExpirationDuration(expirationTimeInMillis)
            .setTransitionTypes(GEOFENCE_TRANSITION_ENTER or GEOFENCE_TRANSITION_EXIT)
            .build()
    }

}