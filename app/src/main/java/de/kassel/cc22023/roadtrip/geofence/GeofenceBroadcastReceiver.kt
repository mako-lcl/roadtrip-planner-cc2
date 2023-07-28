package de.kassel.cc22023.roadtrip.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import de.kassel.cc22023.roadtrip.util.sendNotification
import timber.log.Timber

object GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent) ?: return

        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Timber.e(errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            // Get the transition details as a String.
            val geofenceTransitionDetails = getGeofenceTransitionDetails(
                geofenceTransition,
                triggeringGeofences
            )

            // Send notification and log the transition details.
            sendNotification("YOU ENTERED FENCE", "YOU ENTERED FENCE!!!", context)
            Timber.d(geofenceTransitionDetails)
        }
    }

    fun getGeofenceTransitionDetails(
        transitionType: Int,
        triggeringGeofences: List<Geofence>?
    ): String {
        if (triggeringGeofences == null || triggeringGeofences.isEmpty()) {
            return "No geofences triggered."
        }

        val geofenceNames = triggeringGeofences.joinToString { it.requestId }
        val transitionTypeName = when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "ENTERED"
            else -> "UNKNOWN"
        }

        return "Geofence(s) $geofenceNames were $transitionTypeName."
    }
}
