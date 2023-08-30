package de.kassel.cc22023.roadtrip.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import dagger.hilt.android.AndroidEntryPoint
import de.kassel.cc22023.roadtrip.data.preferences.PreferenceStore
import de.kassel.cc22023.roadtrip.data.repository.RoadtripRepository
import de.kassel.cc22023.roadtrip.data.repository.database.NotificationType
import de.kassel.cc22023.roadtrip.floor_detection.FloorService
import de.kassel.cc22023.roadtrip.util.sendNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(Dispatchers.IO)

    @Inject
    lateinit var repository: RoadtripRepository

    @Inject
    lateinit var prefs: PreferenceStore

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("geofence detected")

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
                prefs.currentTrip.collect {tripId ->
                    val packingList = repository.getPackingList()

                    val ids = triggeringGeofences.map { it.requestId }

                    val items = packingList.filter { ids.contains(it.id.toString()) && it.tripId == tripId }

                    val locationItems = items.filter { it.notificationType == NotificationType.LOCATION }
                    val floorItems = items.filter { it.notificationType == NotificationType.FLOOR }

                    if (locationItems.isNotEmpty()) {
                        val itemsString = locationItems.joinToString("\n") {
                            "• ${it.name}"
                        }
                        val notificationTitle = "Don't forget to pack your bag!"
                        val notificationContent = "Tap to see more..."
                        val bigText = "You have setup notification for the following items:\n$itemsString"

                        // Send notification and log the transition details.
                        sendNotification(notificationTitle, notificationContent, bigText, context)
                        Timber.d(geofenceTransitionDetails)

                        repository.resetNotification(locationItems)
                    }

                    if (floorItems.isNotEmpty()) {
                        Timber.d("$floorItems")
                        context.startService(Intent(context, FloorService::class.java))
                    }
                }
            }
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Timber.d("exited geofence")
            context.stopService(Intent(context, FloorService::class.java))
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
            Geofence.GEOFENCE_TRANSITION_EXIT -> "EXITED"
            else -> "UNKNOWN"
        }

        return "Geofence(s) $geofenceNames were $transitionTypeName."
    }
}
