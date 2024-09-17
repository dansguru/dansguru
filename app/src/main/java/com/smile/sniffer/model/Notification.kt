package com.smile.sniffer.model

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object AppNotificationManager {
    fun sendNotification(context: Context, title: String, message: String) {
        // Check if the POST_NOTIFICATIONS permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission not granted; handle as needed
                return
            }
        }

        val notificationManager = NotificationManagerCompat.from(context)

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "heads_up_notifications" // Updated channel ID
            val channelName = "Heads-up Notifications" // Updated channel name
            val importance = NotificationManager.IMPORTANCE_HIGH // Set to high importance
            val notificationChannel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel for heads-up notifications"
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notification = NotificationCompat.Builder(context, "heads_up_notifications")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Set priority to high
            .setCategory(NotificationCompat.CATEGORY_MESSAGE) // Set category for messaging
            .setAutoCancel(true) // Automatically dismiss the notification when tapped
            .build()

        notificationManager.notify(1, notification)
    }
}
