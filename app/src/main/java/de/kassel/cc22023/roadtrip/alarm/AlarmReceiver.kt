package de.kassel.cc22023.roadtrip.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import de.kassel.cc22023.roadtrip.data.repository.RoadtripRepository
import de.kassel.cc22023.roadtrip.data.repository.database.NotificationType
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItem
import de.kassel.cc22023.roadtrip.util.sendNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject

const val ALARM_INTENT = 393482

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var repository: RoadtripRepository

    override fun onReceive(context: Context, intent: Intent) {
        val itemName = intent.extras?.getString("name") ?: return
        val itemId = intent.extras?.getLong("id") ?: return
        val tripId = intent.extras?.getLong("tripId") ?: return
        val checked = intent.extras?.getBoolean("checked") ?: return
        val image = intent.extras?.getString("image") ?: return

        sendNotification("Don't forget to pack your bag!", "Expand to see more", "You have added a reminder for the following item: $itemName", context)

        CoroutineScope(Dispatchers.IO).launch {
            repository.getPackingList()

            repository.resetNotification(listOf(
                PackingItem(itemId, tripId, itemName, NotificationType.NONE, checked, null, 0, 0.0, 0.0, image)
            ))
        }
    }
}

fun setAlarm(item: PackingItem, context: Context) {
    val time = item.time ?: return
    // creating alarmManager instance
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    // adding intent and pending intent to go to AlarmReceiver Class in future
    val intent = Intent(context, AlarmReceiver::class.java)
    intent.putExtra("name", item.name)
    intent.putExtra("id", item.id)
    intent.putExtra("checked", item.isChecked)
    intent.putExtra("image", item.image)
    intent.putExtra("tripId", item.tripId)
    val pendingIntent = PendingIntent.getBroadcast(context, ALARM_INTENT, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT)
    val date = Instant.ofEpochSecond(time)
    Timber.d("epoch millis: ${date.toEpochMilli()}")
    // creating clockInfo instance

    // setting the alarm
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.toEpochMilli(), pendingIntent)
}