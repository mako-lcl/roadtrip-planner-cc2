package de.kassel.cc22023.roadtrip.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import de.kassel.cc22023.roadtrip.R

const val NOTIFICATION_CHANNEL_ID = "PackingListChannelID"

fun createNotificationChannel(context: Context) {
    val name = "Packing notifications"
    val descriptionText = "Packing list notifications!"
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
        description = descriptionText
    }

    val notificationManager: NotificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}

fun sendNotificationWithRuntime(title: String, content: String, context: Context) {
    val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(title)
        .setContentText(content)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                notify(0, builder.build())
            }
        } else {
            notify(0, builder.build())
        }
    }
}