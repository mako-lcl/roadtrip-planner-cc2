package de.kassel.cc22023.roadtrip.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import dagger.hilt.android.AndroidEntryPoint
import de.kassel.cc22023.roadtrip.data.RoadtripRepository
import de.kassel.cc22023.roadtrip.data.local.database.PackingItem
import de.kassel.cc22023.roadtrip.util.sendNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
class GeofenceBroadcastReceiver : BroadcastReceiver() {
    val scope = CoroutineScope(Dispatchers.IO)

    @Inject
    lateinit var repository: RoadtripRepository

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
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            val triggeringGeofences = geofencingEvent.triggeringGeofences ?: return

            // Get the transition details as a String.
            val geofenceTransitionDetails = getGeofenceTransitionDetails(
                geofenceTransition,
                triggeringGeofences
            )

            scope.launch {
                val packingList = repository.getPackingList()

                val location = repository.getLocation()

                val ids = triggeringGeofences.map { it.requestId }

                val items = packingList.filter { ids.contains(it.id.toString()) }
                val itemsString = items.joinToString("\n") {
                    "• ${it.name}"
                }
                val notificationTitle = "Don't forget to pack your bag!"
                val notificationContent = "Tap to see more..."
                val bigText = "You have setup notification for the following items:\n$itemsString"

                // Send notification and log the transition details.
                sendNotification(notificationTitle, notificationContent, bigText, context)
                Timber.d(geofenceTransitionDetails)
            }
        }
    }

    private fun getGeofenceTransitionDetails(
        transitionType: Int,
        triggeringGeofences: List<Geofence>?
    ): String {
        if (triggeringGeofences.isNullOrEmpty()) {
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
