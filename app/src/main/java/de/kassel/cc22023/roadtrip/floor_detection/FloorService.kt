package de.kassel.cc22023.roadtrip.floor_detection

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import de.kassel.cc22023.roadtrip.R
import de.kassel.cc22023.roadtrip.data.preferences.PreferenceStore
import de.kassel.cc22023.roadtrip.data.repository.RoadtripRepository
import de.kassel.cc22023.roadtrip.data.repository.database.NotificationType
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItem
import de.kassel.cc22023.roadtrip.util.isOnFloor
import de.kassel.cc22023.roadtrip.util.sendNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


private const val FOREGROUND_ID = 43534531
const val SERVICE_CHANNEL = "FloorChannel"
const val FOREGROUND_NOTIFICATION_ID = "Notification Channel"

@AndroidEntryPoint
class FloorService : Service() {
    private lateinit var items: List<PackingItem>
    private lateinit var listener: SensorEventListener
    private var startedListening = false

    @Inject
    lateinit var repository: RoadtripRepository

    @Inject
    lateinit var prefs: PreferenceStore

    var baseHeight = -1.0
    var tripId = -1L

    private fun collectPrefs() {
        CoroutineScope(Dispatchers.IO).launch {
            prefs.height.collect {
                Timber.d("found baseheight in service: $it")
                baseHeight = it
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            prefs.currentTrip.collect {
                Timber.d("found trip id in service: $it")
                tripId = it
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createForegroundChannel(applicationContext)

        val notification: Notification = Notification.Builder(this, FOREGROUND_NOTIFICATION_ID)
            .setOngoing(true)
            .setContentTitle(getText(R.string.notification_title))
            .setContentText(getText(R.string.notification_message))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(FOREGROUND_ID, notification)

        collectPrefs()

        startMonitoringFloors()

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startMonitoringFloors() {
        val mSensorManager = applicationContext.getSystemService(SENSOR_SERVICE) as SensorManager
        val sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        CoroutineScope(Dispatchers.IO).launch {
            items = repository.getPackingList().filter { it.notificationType == NotificationType.FLOOR }
        }

        listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null) {
                    val pressure = event.values[0]
                    val altitude = SensorManager.getAltitude(
                        SensorManager.PRESSURE_STANDARD_ATMOSPHERE,
                        pressure
                    )

                    if (altitude > 0 && baseHeight > 0 && tripId != -1L) {
                        val foundItems = items.filter { isOnFloor(altitude.toDouble(), baseHeight, it.floor) && it.tripId == tripId }

                        if (foundItems.isNotEmpty()) {
                            Timber.d("found items: $foundItems")
                            sendNotification(
                                "Don't forget to pack your bag!",
                                "Expand to see more",
                                "You have added a reminder for the following item: ${foundItems.joinToString("\n") { "• ${it.name}" }}",
                                applicationContext)

                            CoroutineScope(Dispatchers.IO).launch {
                                repository.resetNotification(foundItems)
                            }

                            stopService()
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }
        }

        if (sensor != null) {
            startedListening = true
            mSensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            stopService()
        }
    }

    private fun stopService() {
        if (startedListening) {
            val mSensorManager = applicationContext.getSystemService(SENSOR_SERVICE) as SensorManager
            mSensorManager.unregisterListener(listener)
            startedListening = false
        }
        stopSelf()
    }
}

fun createForegroundChannel(ctx: Context) {
    val channel = NotificationChannel(
        FOREGROUND_NOTIFICATION_ID,
        "Floor detection channel",
        NotificationManager.IMPORTANCE_DEFAULT
    )

    (ctx.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?)!!.createNotificationChannel(
        channel
    )
}