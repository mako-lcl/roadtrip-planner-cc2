package de.kassel.cc22023.roadtrip.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItem
import de.kassel.cc22023.roadtrip.ui.MainActivity
import de.kassel.cc22023.roadtrip.util.sendNotification
import timber.log.Timber
import java.time.Instant

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val itemName = intent.extras?.getString("name") ?: return

        sendNotification("Don't forget to pack your bag!", "Expand to see more", "You have added a reminder for the following item: $itemName", context)
    }
}

fun setAlarm(item: PackingItem, context: Context) {
    val time = item.time ?: return
    // creating alarmManager instance
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    // adding intent and pending intent to go to AlarmReceiver Class in future
    val intent = Intent(context, AlarmReceiver::class.java)
    intent.putExtra("name", item.name)
    val pendingIntent = PendingIntent.getBroadcast(context, item.id, intent, PendingIntent.FLAG_IMMUTABLE)
    // when using setAlarmClock() it displays a notification until alarm rings and when pressed it takes us to mainActivity
    val mainActivityIntent = Intent(context, MainActivity::class.java)
    val basicPendingIntent = PendingIntent.getActivity(context, item.id, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE)
    val date = Instant.ofEpochSecond(time)
    Timber.d("epoch millis: ${date.toEpochMilli()}")
    // creating clockInfo instance
    val clockInfo = AlarmManager.AlarmClockInfo(date.toEpochMilli(), basicPendingIntent)

    // setting the alarm
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.toEpochMilli(), pendingIntent)
}